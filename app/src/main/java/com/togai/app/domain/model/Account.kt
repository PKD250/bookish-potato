package com.togai.app.domain.model

data class Account(
    val id: Long = 0,
    val name: String,
    val type: AccountType,
    val bankName: String? = null,
    val accountNumber: String? = null,
    val balance: Double = 0.0,
    val creditLimit: Double? = null,
    val billingCycleDay: Int? = null,
    val billingDueDate: Long? = null,
    val colorHex: String = "#6366F1",
    val isDefault: Boolean = false
)
