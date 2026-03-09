package com.togai.app.domain.usecase.transaction

import com.togai.app.domain.model.CategorySpending
import com.togai.app.domain.model.MonthlyStats
import com.togai.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

class GetMonthlyStatsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    fun getStats(yearMonth: YearMonth): Flow<MonthlyStats> {
        val (start, end) = getMonthRange(yearMonth)
        return repository.getMonthlyStats(start, end)
    }

    fun getCategorySpending(yearMonth: YearMonth): Flow<List<CategorySpending>> {
        val (start, end) = getMonthRange(yearMonth)
        return repository.getCategorySpending(start, end)
    }

    fun getTodaySpending(): Flow<Double> {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now()
        val startOfDay = today.atStartOfDay().atZone(zone).toInstant().toEpochMilli()
        val endOfDay = today.atTime(LocalTime.MAX).atZone(zone).toInstant().toEpochMilli()
        return repository.getTodaySpending(startOfDay, endOfDay)
    }

    private fun getMonthRange(yearMonth: YearMonth): Pair<Long, Long> {
        val zone = ZoneId.systemDefault()
        val start = yearMonth.atDay(1).atStartOfDay().atZone(zone).toInstant().toEpochMilli()
        val end = yearMonth.atEndOfMonth().atTime(LocalTime.MAX).atZone(zone).toInstant().toEpochMilli()
        return Pair(start, end)
    }
}
