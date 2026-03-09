package com.togai.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.togai.app.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

data class CategorySpendingTuple(
    val categoryName: String,
    val colorHex: String,
    val totalAmount: Double
)

data class MonthlyStatsTuple(
    val totalIncome: Double?,
    val totalExpense: Double?
)

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY transaction_date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE transaction_date BETWEEN :startDate AND :endDate ORDER BY transaction_date DESC")
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY transaction_date DESC")
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE category_id = :categoryId ORDER BY transaction_date DESC")
    fun getTransactionsByCategory(categoryId: Long): Flow<List<TransactionEntity>>

    @Query("""
        SELECT * FROM transactions
        WHERE (description LIKE '%' || :query || '%' OR bank_name LIKE '%' || :query || '%')
        ORDER BY transaction_date DESC
    """)
    fun searchTransactions(query: String): Flow<List<TransactionEntity>>

    @Query("""
        SELECT c.name AS categoryName, c.color_hex AS colorHex, SUM(t.amount) AS totalAmount
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.id
        WHERE t.type = 'DEBIT' AND t.transaction_date BETWEEN :startDate AND :endDate
        GROUP BY t.category_id
        ORDER BY totalAmount DESC
    """)
    fun getCategorySpending(startDate: Long, endDate: Long): Flow<List<CategorySpendingTuple>>

    @Query("""
        SELECT
            SUM(CASE WHEN type = 'CREDIT' THEN amount ELSE 0 END) AS totalIncome,
            SUM(CASE WHEN type = 'DEBIT' THEN amount ELSE 0 END) AS totalExpense
        FROM transactions
        WHERE transaction_date BETWEEN :startDate AND :endDate
    """)
    fun getMonthlyStats(startDate: Long, endDate: Long): Flow<MonthlyStatsTuple>

    @Query("SELECT SUM(CASE WHEN type = 'DEBIT' THEN amount ELSE 0 END) FROM transactions WHERE transaction_date >= :startOfDay AND transaction_date < :endOfDay")
    fun getTodaySpending(startOfDay: Long, endOfDay: Long): Flow<Double?>

    @Query("SELECT * FROM transactions ORDER BY transaction_date DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE pending_account_assignment = 1 ORDER BY transaction_date DESC")
    fun getPendingAccountAssignments(): Flow<List<TransactionEntity>>

    @Query("SELECT COUNT(*) FROM transactions WHERE pending_account_assignment = 1")
    fun getPendingAssignmentCount(): Flow<Int>

    @Query("SELECT * FROM transactions WHERE account_id = :accountId ORDER BY transaction_date DESC")
    fun getTransactionsByAccountId(accountId: Long): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM transactions WHERE sms_hash = :hash)")
    suspend fun existsByHash(hash: String): Boolean

    @Query("SELECT COUNT(*) FROM transactions")
    fun getTransactionCount(): Flow<Int>

    @Query("UPDATE transactions SET account_id = :accountId, pending_account_assignment = 0 WHERE id = :transactionId")
    suspend fun assignAccount(transactionId: Long, accountId: Long)
}
