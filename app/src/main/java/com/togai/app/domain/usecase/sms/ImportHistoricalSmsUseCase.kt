package com.togai.app.domain.usecase.sms

import com.togai.app.data.local.dao.AccountDao
import com.togai.app.data.local.dao.TransactionDao
import com.togai.app.data.local.entity.TransactionEntity
import com.togai.app.data.preferences.SyncPreferencesManager
import com.togai.app.domain.model.AccountType
import com.togai.app.domain.repository.ImportProgress
import com.togai.app.domain.repository.SmsRepository
import com.togai.app.sms.TransactionCategorizer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import java.security.MessageDigest
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class ImportHistoricalSmsUseCase @Inject constructor(
    private val smsRepository: SmsRepository,
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val categorizer: TransactionCategorizer,
    private val syncPreferencesManager: SyncPreferencesManager
) {
    fun invoke(): Flow<ImportProgress> {
        val sinceTimestamp = syncPreferencesManager.lastSyncTimestamp.let { stored ->
            if (stored == 0L) {
                // First run: default to start of today so we don't pull all-time history
                LocalDate.now()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant().toEpochMilli()
            } else {
                stored
            }
        }
        val importStartTime = System.currentTimeMillis()

        return smsRepository.importHistoricalSms(sinceTimestamp).onEach { progress ->
            val parsed = progress.lastParsed ?: return@onEach

            val smsHash = MessageDigest.getInstance("SHA-256")
                .digest(parsed.rawSms.trim().lowercase().toByteArray())
                .joinToString("") { "%02x".format(it) }

            if (transactionDao.existsByHash(smsHash)) return@onEach

            val categoryId = categorizer.categorize(parsed.merchant, parsed.rawSms, parsed.type)

            // Account matching
            var resolvedAccountId: Long? = null
            var pendingAssignment = false
            val accNum = parsed.paymentAccountNumber ?: parsed.accountNumber
            if (accNum != null) {
                val account = accountDao.findByAccountNumber(accNum)
                if (account != null) {
                    resolvedAccountId = account.id
                    if (account.type == AccountType.CREDIT_CARD.name && parsed.dueDateEpoch != null) {
                        accountDao.updateBillingDueDate(account.id, parsed.dueDateEpoch)
                    }
                } else {
                    pendingAssignment = true
                }
            } else if (parsed.paymentConfidence < 1.0f) {
                pendingAssignment = true
            }

            val entity = TransactionEntity(
                amount = parsed.amount,
                type = parsed.type.name,
                categoryId = categoryId,
                description = parsed.merchant,
                accountNumber = parsed.accountNumber,
                bankName = parsed.bankName,
                transactionDate = parsed.date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                createdAt = System.currentTimeMillis(),
                smsBody = parsed.rawSms,
                isManual = false,
                referenceId = parsed.referenceId,
                smsHash = smsHash,
                accountId = resolvedAccountId,
                pendingAccountAssignment = pendingAssignment
            )

            transactionDao.insert(entity)
        }.onCompletion { cause ->
            if (cause == null) {
                syncPreferencesManager.lastSyncTimestamp = importStartTime
            }
        }
    }
}
