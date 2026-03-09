package com.togai.app.domain.model

import java.time.LocalDateTime

data class SmsParsedData(
    val amount: Double,
    val type: TransactionType,
    val accountNumber: String?,
    val bankName: String?,
    val date: LocalDateTime,
    val merchant: String,
    val balance: Double?,
    val referenceId: String?,
    val rawSms: String,
    val paymentMethodType: AccountType? = null,
    val paymentAccountNumber: String? = null,
    val paymentConfidence: Float = 0f,
    val dueDateEpoch: Long? = null
)
