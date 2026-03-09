package com.togai.app.data.repository

import com.togai.app.data.local.dao.SavingsGoalDao
import com.togai.app.data.local.entity.SavingsGoalEntity
import com.togai.app.domain.model.SavingsGoal
import com.togai.app.domain.repository.SavingsGoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavingsGoalRepositoryImpl @Inject constructor(
    private val savingsGoalDao: SavingsGoalDao
) : SavingsGoalRepository {

    override fun getAllSavingsGoals(): Flow<List<SavingsGoal>> {
        return savingsGoalDao.getAllSavingsGoals().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getActiveSavingsGoals(): Flow<List<SavingsGoal>> {
        return savingsGoalDao.getActiveSavingsGoals().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getSavingsGoalById(id: Long): SavingsGoal? {
        return savingsGoalDao.getSavingsGoalById(id)?.toDomain()
    }

    override suspend fun insert(savingsGoal: SavingsGoal): Long {
        return savingsGoalDao.insert(savingsGoal.toEntity())
    }

    override suspend fun update(savingsGoal: SavingsGoal) {
        savingsGoalDao.update(savingsGoal.toEntity())
    }

    override suspend fun delete(savingsGoal: SavingsGoal) {
        savingsGoalDao.delete(savingsGoal.toEntity())
    }

    private fun SavingsGoalEntity.toDomain(): SavingsGoal {
        return SavingsGoal(
            id = id,
            name = name,
            targetAmount = targetAmount,
            currentAmount = currentAmount,
            startDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(startDate), ZoneId.systemDefault()),
            targetDate = targetDate?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()) },
            colorHex = colorHex,
            isCompleted = isCompleted
        )
    }

    private fun SavingsGoal.toEntity(): SavingsGoalEntity {
        return SavingsGoalEntity(
            id = id,
            name = name,
            targetAmount = targetAmount,
            currentAmount = currentAmount,
            startDate = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            targetDate = targetDate?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
            colorHex = colorHex,
            isCompleted = isCompleted,
            createdAt = System.currentTimeMillis()
        )
    }
}
