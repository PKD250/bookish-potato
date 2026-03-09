package com.togai.app.domain.repository

import com.togai.app.domain.model.CategorySpending
import com.togai.app.domain.model.MonthlyStats
import com.togai.app.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>>
    fun getTransactionsByType(type: String): Flow<List<Transaction>>
    fun searchTransactions(query: String): Flow<List<Transaction>>
    fun getCategorySpending(startDate: Long, endDate: Long): Flow<List<CategorySpending>>
    fun getMonthlyStats(startDate: Long, endDate: Long): Flow<MonthlyStats>
    fun getTodaySpending(startOfDay: Long, endOfDay: Long): Flow<Double>
    fun getRecentTransactions(limit: Int): Flow<List<Transaction>>
    fun getTransactionCount(): Flow<Int>
    fun getPendingAccountAssignments(): Flow<List<Transaction>>
    fun getPendingAssignmentCount(): Flow<Int>
    fun getTransactionsByAccountId(accountId: Long): Flow<List<Transaction>>
    suspend fun insert(transaction: Transaction): Long
    suspend fun update(transaction: Transaction)
    suspend fun deleteById(id: Long)
    suspend fun existsByHash(hash: String): Boolean
    suspend fun assignAccount(transactionId: Long, accountId: Long)
}
