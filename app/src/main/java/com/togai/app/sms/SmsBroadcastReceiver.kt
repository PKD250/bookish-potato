package com.togai.app.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.togai.app.data.local.dao.AccountDao
import com.togai.app.data.local.dao.TransactionDao
import com.togai.app.data.local.entity.TransactionEntity
import com.togai.app.domain.model.AccountType
import com.togai.app.domain.model.TransactionType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.time.ZoneId
import javax.inject.Inject

@AndroidEntryPoint
class SmsBroadcastReceiver : BroadcastReceiver() {

    @Inject lateinit var smsParser: SmsParser
    @Inject lateinit var categorizer: TransactionCategorizer
    @Inject lateinit var transactionDao: TransactionDao
    @Inject lateinit var accountDao: AccountDao

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val fullBody = messages.joinToString("") { it.messageBody }
        val sender = messages.firstOrNull()?.originatingAddress ?: return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val parsed = smsParser.parse(fullBody, sender) ?: return@launch

                val smsHash = MessageDigest.getInstance("SHA-256")
                    .digest(fullBody.trim().lowercase().toByteArray())
                    .joinToString("") { "%02x".format(it) }

                if (transactionDao.existsByHash(smsHash)) return@launch

                val categoryId = categorizer.categorize(
                    parsed.merchant, parsed.rawSms, parsed.type
                )

                // Try to match to an existing account
                var resolvedAccountId: Long? = null
                var pendingAssignment = false
                val accNum = parsed.paymentAccountNumber ?: parsed.accountNumber
                if (accNum != null) {
                    val account = accountDao.findByAccountNumber(accNum)
                    if (account != null) {
                        resolvedAccountId = account.id
                        // Update credit card due date if extracted
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
            } finally {
                pendingResult.finish()
            }
        }
    }
}
