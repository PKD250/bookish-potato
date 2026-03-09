package com.togai.app.domain.model

import java.time.LocalDateTime

data class Budget(
    val id: Long = 0,
    val categoryId: Long? = null,
    val amountLimit: Double,
    val periodType: BudgetPeriod,
    val startDate: LocalDateTime,
    val isActive: Boolean = true
)

enum class BudgetPeriod {
    MONTHLY,
    WEEKLY
}
