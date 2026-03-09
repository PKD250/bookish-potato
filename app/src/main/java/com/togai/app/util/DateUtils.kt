package com.togai.app.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object DateUtils {
    fun startOfMonth(yearMonth: YearMonth): Long {
        return yearMonth.atDay(1).atStartOfDay()
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun endOfMonth(yearMonth: YearMonth): Long {
        return yearMonth.atEndOfMonth().atTime(LocalTime.MAX)
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun startOfDay(date: LocalDate = LocalDate.now()): Long {
        return date.atStartOfDay()
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun endOfDay(date: LocalDate = LocalDate.now()): Long {
        return date.atTime(LocalTime.MAX)
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun currentMonth(): YearMonth = YearMonth.now()

    fun toLocalDateTime(epochMillis: Long): LocalDateTime {
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(epochMillis),
            ZoneId.systemDefault()
        )
    }

    fun formatRelativeTime(timestamp: Long): String {
        val now = Instant.now()
        val then = Instant.ofEpochMilli(timestamp)
        val minutes = ChronoUnit.MINUTES.between(then, now)
        val hours = ChronoUnit.HOURS.between(then, now)
        val days = ChronoUnit.DAYS.between(then, now)
        return when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "$minutes minute${if (minutes == 1L) "" else "s"} ago"
            hours < 24 -> "$hours hour${if (hours == 1L) "" else "s"} ago"
            days == 1L -> "Yesterday"
            days < 7 -> "$days days ago"
            else -> "${days / 7} week${if (days / 7 == 1L) "" else "s"} ago"
        }
    }
}
