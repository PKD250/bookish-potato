package com.togai.app.domain.usecase.savings

import com.togai.app.domain.model.SavingsGoal
import com.togai.app.domain.repository.SavingsGoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavingsGoalsUseCase @Inject constructor(
    private val repository: SavingsGoalRepository
) {
    operator fun invoke(): Flow<List<SavingsGoal>> = repository.getAllSavingsGoals()
    fun active(): Flow<List<SavingsGoal>> = repository.getActiveSavingsGoals()
}

class AddSavingsGoalUseCase @Inject constructor(
    private val repository: SavingsGoalRepository
) {
    suspend operator fun invoke(goal: SavingsGoal): Long = repository.insert(goal)
}

class UpdateSavingsGoalUseCase @Inject constructor(
    private val repository: SavingsGoalRepository
) {
    suspend operator fun invoke(goal: SavingsGoal) = repository.update(goal)
}

class DeleteSavingsGoalUseCase @Inject constructor(
    private val repository: SavingsGoalRepository
) {
    suspend operator fun invoke(goal: SavingsGoal) = repository.delete(goal)
}
