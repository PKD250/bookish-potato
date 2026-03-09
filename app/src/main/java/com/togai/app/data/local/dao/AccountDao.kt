package com.togai.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.togai.app.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query("SELECT * FROM accounts ORDER BY is_default DESC, name ASC")
    fun getAllAccounts(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE type = :type ORDER BY name ASC")
    fun getAccountsByType(type: String): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getAccountById(id: Long): AccountEntity?

    @Query("SELECT * FROM accounts WHERE account_number = :accountNumber LIMIT 1")
    suspend fun findByAccountNumber(accountNumber: String): AccountEntity?

    @Query("SELECT * FROM accounts WHERE is_default = 1 LIMIT 1")
    suspend fun getDefaultAccount(): AccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: AccountEntity): Long

    @Update
    suspend fun update(account: AccountEntity)

    @Query("DELETE FROM accounts WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE accounts SET billing_due_date = :dueDate WHERE id = :id")
    suspend fun updateBillingDueDate(id: Long, dueDate: Long)
}
