package com.togai.app.data.repository

import com.togai.app.data.local.dao.BudgetDao
import com.togai.app.data.local.entity.BudgetEntity
import com.togai.app.domain.model.Budget
import com.togai.app.domain.model.BudgetPeriod
import com.togai.app.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao
) : BudgetRepository {

    override fun getActiveBudgets(): Flow<List<Budget>> {
        return budgetDao.getActiveBudgets().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getBudgetByCategory(categoryId: Long): Budget? {
        return budgetDao.getBudgetByCategory(categoryId)?.toDomain()
    }

    override suspend fun insert(budget: Budget): Long {
        return budgetDao.insert(budget.toEntity())
    }

    override suspend fun update(budget: Budget) {
        budgetDao.update(budget.toEntity())
    }

    override suspend fun delete(budget: Budget) {
        budgetDao.delete(budget.toEntity())
    }

    private fun BudgetEntity.toDomain(): Budget {
        return Budget(
            id = id,
            categoryId = categoryId,
            amountLimit = amountLimit,
            periodType = BudgetPeriod.valueOf(periodType),
            startDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(startDate), ZoneId.systemDefault()),
            isActive = isActive
        )
    }

    private fun Budget.toEntity(): BudgetEntity {
        return BudgetEntity(
            id = id,
            categoryId = categoryId,
            amountLimit = amountLimit,
            periodType = periodType.name,
            startDate = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            isActive = isActive
        )
    }
}
