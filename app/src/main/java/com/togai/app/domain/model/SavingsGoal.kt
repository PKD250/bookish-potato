package com.togai.app.domain.model

import java.time.LocalDateTime

data class SavingsGoal(
    val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val startDate: LocalDateTime,
    val targetDate: LocalDateTime? = null,
    val colorHex: String,
    val isCompleted: Boolean = false
) {
    val progressPercent: Float
        get() = if (targetAmount > 0) (currentAmount / targetAmount * 100).toFloat().coerceIn(0f, 100f) else 0f
}
