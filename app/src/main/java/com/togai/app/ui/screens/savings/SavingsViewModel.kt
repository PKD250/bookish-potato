package com.togai.app.ui.screens.savings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.togai.app.domain.model.MonthlyStats
import com.togai.app.domain.model.SavingsGoal
import com.togai.app.domain.usecase.savings.AddSavingsGoalUseCase
import com.togai.app.domain.usecase.savings.DeleteSavingsGoalUseCase
import com.togai.app.domain.usecase.savings.GetSavingsGoalsUseCase
import com.togai.app.domain.usecase.savings.UpdateSavingsGoalUseCase
import com.togai.app.domain.usecase.transaction.GetMonthlyStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.YearMonth
import javax.inject.Inject

data class SavingsUiState(
    val savingsGoals: List<SavingsGoal> = emptyList(),
    val monthlyStats: MonthlyStats = MonthlyStats(),
    val isLoading: Boolean = true
)

@HiltViewModel
class SavingsViewModel @Inject constructor(
    private val getSavingsGoalsUseCase: GetSavingsGoalsUseCase,
    private val addSavingsGoalUseCase: AddSavingsGoalUseCase,
    private val updateSavingsGoalUseCase: UpdateSavingsGoalUseCase,
    private val deleteSavingsGoalUseCase: DeleteSavingsGoalUseCase,
    private val getMonthlyStatsUseCase: GetMonthlyStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavingsUiState())
    val uiState: StateFlow<SavingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getSavingsGoalsUseCase(),
                getMonthlyStatsUseCase.getStats(YearMonth.now())
            ) { goals, stats ->
                SavingsUiState(
                    savingsGoals = goals,
                    monthlyStats = stats,
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }

    fun addGoal(name: String, targetAmount: Double, colorHex: String) {
        viewModelScope.launch {
            addSavingsGoalUseCase(
                SavingsGoal(
                    name = name,
                    targetAmount = targetAmount,
                    startDate = LocalDateTime.now(),
                    colorHex = colorHex
                )
            )
        }
    }

    fun updateGoalAmount(goal: SavingsGoal, newAmount: Double) {
        viewModelScope.launch {
            updateSavingsGoalUseCase(
                goal.copy(
                    currentAmount = newAmount,
                    isCompleted = newAmount >= goal.targetAmount
                )
            )
        }
    }

    fun deleteGoal(goal: SavingsGoal) {
        viewModelScope.launch {
            deleteSavingsGoalUseCase(goal)
        }
    }
}
