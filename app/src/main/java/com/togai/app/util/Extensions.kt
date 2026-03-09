package com.togai.app.util

import androidx.compose.ui.graphics.Color
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val currencyFormat = DecimalFormat("#,##,##0.00")
private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")

fun Double.formatCurrency(): String {
    return "${Constants.CURRENCY_SYMBOL}${currencyFormat.format(this)}"
}

fun Double.formatCompact(): String {
    return when {
        this >= 10_000_000 -> "${Constants.CURRENCY_SYMBOL}${String.format("%.1f", this / 10_000_000)}Cr"
        this >= 100_000 -> "${Constants.CURRENCY_SYMBOL}${String.format("%.1f", this / 100_000)}L"
        this >= 1_000 -> "${Constants.CURRENCY_SYMBOL}${String.format("%.1f", this / 1_000)}K"
        else -> formatCurrency()
    }
}

fun LocalDateTime.formatDate(): String = this.format(dateFormatter)
fun LocalDateTime.formatTime(): String = this.format(timeFormatter)
fun LocalDateTime.formatDateTime(): String = this.format(dateTimeFormatter)

fun LocalDate.formatDate(): String = this.format(dateFormatter)

fun LocalDateTime.toRelativeDate(): String {
    val today = LocalDate.now()
    val date = this.toLocalDate()
    return when {
        date == today -> "Today"
        date == today.minusDays(1) -> "Yesterday"
        date == today.plusDays(1) -> "Tomorrow"
        else -> date.formatDate()
    }
}

fun String.hexToColor(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (_: Exception) {
        Color.Gray
    }
}
