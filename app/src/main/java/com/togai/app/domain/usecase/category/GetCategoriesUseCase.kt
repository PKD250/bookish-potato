package com.togai.app.domain.usecase.category

import com.togai.app.domain.model.Category
import com.togai.app.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> = repository.getAllCategories()

    fun expenseOnly(): Flow<List<Category>> = repository.getExpenseCategories()

    fun incomeOnly(): Flow<List<Category>> = repository.getIncomeCategories()
}
