package com.togai.app.domain.model

import java.time.LocalDateTime

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val categoryId: Long? = null,
    val categoryName: String? = null,
    val categoryIcon: String? = null,
    val categoryColor: String? = null,
    val description: String,
    val accountNumber: String? = null,
    val bankName: String? = null,
    val transactionDate: LocalDateTime,
    val smsBody: String? = null,
    val isManual: Boolean = false,
    val referenceId: String? = null,
    val accountId: Long? = null,
    val accountName: String? = null,
    val pendingAccountAssignment: Boolean = false
)
