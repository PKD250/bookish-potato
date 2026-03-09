package com.togai.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.togai.app.data.local.entity.SavingsGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsGoalDao {

    @Query("SELECT * FROM savings_goals ORDER BY is_completed ASC, created_at DESC")
    fun getAllSavingsGoals(): Flow<List<SavingsGoalEntity>>

    @Query("SELECT * FROM savings_goals WHERE is_completed = 0 ORDER BY created_at DESC")
    fun getActiveSavingsGoals(): Flow<List<SavingsGoalEntity>>

    @Query("SELECT * FROM savings_goals WHERE id = :id")
    suspend fun getSavingsGoalById(id: Long): SavingsGoalEntity?

    @Insert
    suspend fun insert(savingsGoal: SavingsGoalEntity): Long

    @Update
    suspend fun update(savingsGoal: SavingsGoalEntity)

    @Delete
    suspend fun delete(savingsGoal: SavingsGoalEntity)
}
