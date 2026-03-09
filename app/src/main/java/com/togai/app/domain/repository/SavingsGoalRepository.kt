package com.togai.app.domain.repository

import com.togai.app.domain.model.SavingsGoal
import kotlinx.coroutines.flow.Flow

interface SavingsGoalRepository {
    fun getAllSavingsGoals(): Flow<List<SavingsGoal>>
    fun getActiveSavingsGoals(): Flow<List<SavingsGoal>>
    suspend fun getSavingsGoalById(id: Long): SavingsGoal?
    suspend fun insert(savingsGoal: SavingsGoal): Long
    suspend fun update(savingsGoal: SavingsGoal)
    suspend fun delete(savingsGoal: SavingsGoal)
}
