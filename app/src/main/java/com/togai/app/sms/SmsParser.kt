package com.togai.app.sms

import com.togai.app.domain.model.AccountType
import com.togai.app.domain.model.SmsParsedData
import com.togai.app.domain.model.TransactionType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsParser @Inject constructor() {

    fun parse(smsBody: String, sender: String): SmsParsedData? {
        val body = smsBody.trim()

        if (!isTransactionalSms(body)) return null

        val type = detectTransactionType(body) ?: return null
        val amount = extractAmount(body) ?: return null
        if (amount <= 0) return null

        val accountNumber = extractAccountNumber(body)
        val bankName = detectBank(sender)
        val date = extractDate(body) ?: LocalDateTime.now()
        val merchant = extractMerchant(body) ?: "Unknown"
        val balance = extractBalance(body)
        val referenceId = extractReference(body)
        val (paymentType, paymentAccNum, confidence) = extractPaymentMethod(body)
        val dueDateEpoch = extractDueDate(body)

        return SmsParsedData(
            amount = amount,
            type = type,
            accountNumber = accountNumber,
            bankName = bankName,
            date = date,
            merchant = merchant,
            balance = balance,
            referenceId = referenceId,
            rawSms = body,
            paymentMethodType = paymentType,
            paymentAccountNumber = paymentAccNum,
            paymentConfidence = confidence,
            dueDateEpoch = dueDateEpoch
        )
    }

    fun extractPaymentMethod(body: String): Triple<AccountType?, String?, Float> {
        // Credit card match has highest confidence
        BankPatterns.CREDIT_CARD_PATTERNS.find(body)?.let { match ->
            val last4 = match.groupValues.getOrNull(1)?.takeIf { it.length == 4 }
            return Triple(AccountType.CREDIT_CARD, last4, 1.0f)
        }
        // UPI implies savings/current account
        if (BankPatterns.UPI_PATTERNS.containsMatchIn(body)) {
            val accNum = BankPatterns.DEBIT_ACCOUNT_PATTERNS.find(body)?.groupValues?.getOrNull(1)
            return Triple(AccountType.SAVINGS, accNum, 0.9f)
        }
        // Explicit savings/current account mention
        BankPatterns.DEBIT_ACCOUNT_PATTERNS.find(body)?.let { match ->
            val accNum = match.groupValues.getOrNull(1)
            return Triple(AccountType.SAVINGS, accNum, 0.85f)
        }
        return Triple(null, null, 0f)
    }

    private fun extractDueDate(body: String): Long? {
        val match = BankPatterns.DUE_DATE_PATTERNS.find(body) ?: return null
        val raw = match.groupValues.getOrNull(1)?.trim() ?: return null
        val formats = listOf(
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd MMM yy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)
        )
        for (fmt in formats) {
            try {
                val date = LocalDate.parse(raw, fmt)
                return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            } catch (_: DateTimeParseException) { }
        }
        return null
    }

    private fun isTransactionalSms(body: String): Boolean {
        val lower = body.lowercase()

        if (BankPatterns.NON_TRANSACTIONAL_KEYWORDS.any { lower.contains(it) }) {
            if (lower.contains("cashback") && !lower.contains("cashback offer")) {
                // cashback transaction - allow
            } else {
                return false
            }
        }

        val hasCurrency = lower.contains("rs") || lower.contains("inr") || lower.contains("rupee")
        if (!hasCurrency) return false

        val hasDebit = BankPatterns.DEBIT_KEYWORDS.any { lower.contains(it) }
        val hasCredit = BankPatterns.CREDIT_KEYWORDS.any { lower.contains(it) }
        return hasDebit || hasCredit
    }

    private fun detectTransactionType(body: String): TransactionType? {
        val lower = body.lowercase()
        val debitMatch = BankPatterns.DEBIT_REGEX.find(lower)
        val creditMatch = BankPatterns.CREDIT_REGEX.find(lower)

        return when {
            debitMatch != null && creditMatch != null -> {
                if (debitMatch.range.first < creditMatch.range.first) TransactionType.DEBIT
                else TransactionType.CREDIT
            }
            debitMatch != null -> TransactionType.DEBIT
            creditMatch != null -> TransactionType.CREDIT
            else -> null
        }
    }

    fun extractAmount(body: String): Double? {
        for (pattern in BankPatterns.AMOUNT_PATTERNS) {
            val match = pattern.find(body)
            if (match != null) {
                val amountStr = match.groupValues[1].replace(",", "")
                val amount = amountStr.toDoubleOrNull()
                if (amount != null && amount > 0) return amount
            }
        }
        return null
    }

    fun detectBank(sender: String): String? {
        val upperSender = sender.uppercase().replace("-", "")
        for ((code, bankName) in BankPatterns.BANK_SENDER_MAP) {
            if (upperSender.contains(code)) return bankName
        }
        return null
    }

    private fun extractAccountNumber(body: String): String? {
        for (pattern in BankPatterns.ACCOUNT_PATTERNS) {
            val match = pattern.find(body)
            if (match != null) return match.groupValues[1]
        }
        return null
    }

    private fun extractDate(body: String): LocalDateTime? {
        // dd-mm-yyyy or dd/mm/yyyy
        BankPatterns.DATE_PATTERNS[0].find(body)?.let { match ->
            try {
                val day = match.groupValues[1].toInt()
                val month = match.groupValues[2].toInt()
                var year = match.groupValues[3].toInt()
                if (year < 100) year += 2000
                return LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.now())
            } catch (_: Exception) { }
        }

        // dd-Mon-yyyy
        BankPatterns.DATE_PATTERNS[1].find(body)?.let { match ->
            try {
                val day = match.groupValues[1].toInt()
                val month = BankPatterns.MONTH_MAP[match.groupValues[2].lowercase()] ?: return@let
                var year = match.groupValues[3].toInt()
                if (year < 100) year += 2000
                return LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.now())
            } catch (_: Exception) { }
        }

        // Mon dd, yyyy
        BankPatterns.DATE_PATTERNS[2].find(body)?.let { match ->
            try {
                val month = BankPatterns.MONTH_MAP[match.groupValues[1].lowercase()] ?: return@let
                val day = match.groupValues[2].toInt()
                val year = match.groupValues[3].toInt()
                return LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.now())
            } catch (_: Exception) { }
        }

        // ddMonYY compact
        BankPatterns.DATE_PATTERNS[3].find(body)?.let { match ->
            try {
                val day = match.groupValues[1].toInt()
                val month = BankPatterns.MONTH_MAP[match.groupValues[2].lowercase()] ?: return@let
                var year = match.groupValues[3].toInt()
                if (year < 100) year += 2000
                return LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.now())
            } catch (_: Exception) { }
        }

        return null
    }

    private fun extractMerchant(body: String): String? {
        for (pattern in BankPatterns.MERCHANT_PATTERNS) {
            val match = pattern.find(body)
            if (match != null) {
                val merchant = match.groupValues[1].trim()
                if (merchant.isNotBlank() && merchant.length > 1) return merchant
            }
        }
        return null
    }

    private fun extractBalance(body: String): Double? {
        for (pattern in BankPatterns.BALANCE_PATTERNS) {
            val match = pattern.find(body)
            if (match != null) {
                return match.groupValues[1].replace(",", "").toDoubleOrNull()
            }
        }
        return null
    }

    private fun extractReference(body: String): String? {
        return BankPatterns.UPI_REF_PATTERN.find(body)?.groupValues?.get(1)
    }
}
