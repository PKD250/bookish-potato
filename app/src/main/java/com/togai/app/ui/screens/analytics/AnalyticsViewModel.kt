package com.togai.app.ui.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.togai.app.domain.model.CategorySpending
import com.togai.app.domain.model.MonthlyStats
import com.togai.app.domain.usecase.transaction.GetMonthlyStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

data class AnalyticsUiState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val stats: MonthlyStats = MonthlyStats(),
    val categorySpending: List<CategorySpending> = emptyList(),
    val monthlyHistory: List<Pair<YearMonth, MonthlyStats>> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getMonthlyStatsUseCase: GetMonthlyStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadData(_uiState.value.selectedMonth)
    }

    fun changeMonth(yearMonth: YearMonth) {
        _uiState.value = _uiState.value.copy(selectedMonth = yearMonth, isLoading = true)
        loadData(yearMonth)
    }

    fun previousMonth() {
        changeMonth(_uiState.value.selectedMonth.minusMonths(1))
    }

    fun nextMonth() {
        val next = _uiState.value.selectedMonth.plusMonths(1)
        if (!next.isAfter(YearMonth.now())) {
            changeMonth(next)
        }
    }

    private fun loadData(yearMonth: YearMonth) {
        viewModelScope.launch {
            combine(
                getMonthlyStatsUseCase.getStats(yearMonth),
                getMonthlyStatsUseCase.getCategorySpending(yearMonth)
            ) { stats, spending ->
                _uiState.value.copy(
                    stats = stats,
                    categorySpending = spending,
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }

        // Load 6-month history
        viewModelScope.launch {
            val history = mutableListOf<Pair<YearMonth, MonthlyStats>>()
            for (i in 5 downTo 0) {
                val month = yearMonth.minusMonths(i.toLong())
                getMonthlyStatsUseCase.getStats(month).collect { stats ->
                    history.add(month to stats)
                    if (history.size == 6) {
                        _uiState.value = _uiState.value.copy(monthlyHistory = history.toList())
                    }
                }
            }
        }
    }
}
