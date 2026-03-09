package com.togai.app.domain.repository

import com.togai.app.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    fun getExpenseCategories(): Flow<List<Category>>
    fun getIncomeCategories(): Flow<List<Category>>
    suspend fun getCategoryById(id: Long): Category?
    suspend fun getCategoryByName(name: String): Category?
    suspend fun insert(category: Category): Long
}
