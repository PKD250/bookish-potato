package com.togai.app.domain.repository

import com.togai.app.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getActiveBudgets(): Flow<List<Budget>>
    suspend fun getBudgetByCategory(categoryId: Long): Budget?
    suspend fun insert(budget: Budget): Long
    suspend fun update(budget: Budget)
    suspend fun delete(budget: Budget)
}
