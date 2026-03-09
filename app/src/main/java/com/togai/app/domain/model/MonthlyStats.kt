package com.togai.app.domain.model

data class MonthlyStats(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0
) {
    val netSavings: Double get() = totalIncome - totalExpense
    val savingsRate: Float get() = if (totalIncome > 0) ((totalIncome - totalExpense) / totalIncome * 100).toFloat() else 0f
}

data class CategorySpending(
    val categoryName: String,
    val colorHex: String,
    val totalAmount: Double,
    val percentage: Float = 0f
)
