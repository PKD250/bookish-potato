package com.togai.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.togai.app.domain.model.CategorySpending
import com.togai.app.domain.model.Transaction
import com.togai.app.domain.repository.TransactionRepository
import com.togai.app.domain.usecase.transaction.GetMonthlyStatsUseCase
import com.togai.app.domain.usecase.transaction.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

data class DashboardUiState(
    val currentMonthIncome: Double = 0.0,
    val currentMonthExpense: Double = 0.0,
    val netBalance: Double = 0.0,
    val todaySpending: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val categorySpending: List<CategorySpending> = emptyList(),
    val pendingAssignmentCount: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getMonthlyStatsUseCase: GetMonthlyStatsUseCase,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
        viewModelScope.launch {
            transactionRepository.getPendingAssignmentCount().collect { count ->
                _uiState.value = _uiState.value.copy(pendingAssignmentCount = count)
            }
        }
    }

    private fun loadDashboardData() {
        val currentMonth = YearMonth.now()
        viewModelScope.launch {
            combine(
                getMonthlyStatsUseCase.getStats(currentMonth),
                getMonthlyStatsUseCase.getCategorySpending(currentMonth),
                getMonthlyStatsUseCase.getTodaySpending(),
                getTransactionsUseCase.recent(10)
            ) { stats, spending, today, recent ->
                _uiState.value.copy(
                    currentMonthIncome = stats.totalIncome,
                    currentMonthExpense = stats.totalExpense,
                    netBalance = stats.netSavings,
                    todaySpending = today,
                    recentTransactions = recent,
                    categorySpending = spending,
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }
}
