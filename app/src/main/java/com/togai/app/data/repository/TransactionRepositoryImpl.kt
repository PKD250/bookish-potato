package com.togai.app.data.repository

import com.togai.app.data.local.dao.CategoryDao
import com.togai.app.data.local.dao.TransactionDao
import com.togai.app.data.local.entity.TransactionEntity
import com.togai.app.domain.model.CategorySpending
import com.togai.app.domain.model.MonthlyStats
import com.togai.app.domain.model.Transaction
import com.togai.app.domain.model.TransactionType
import com.togai.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> =
        transactionDao.getAllTransactions().map { it.map { e -> e.toDomain() } }

    override fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsByDateRange(startDate, endDate).map { it.map { e -> e.toDomain() } }

    override fun getTransactionsByType(type: String): Flow<List<Transaction>> =
        transactionDao.getTransactionsByType(type).map { it.map { e -> e.toDomain() } }

    override fun searchTransactions(query: String): Flow<List<Transaction>> =
        transactionDao.searchTransactions(query).map { it.map { e -> e.toDomain() } }

    override fun getCategorySpending(startDate: Long, endDate: Long): Flow<List<CategorySpending>> =
        transactionDao.getCategorySpending(startDate, endDate).map { tuples ->
            val total = tuples.sumOf { it.totalAmount }
            tuples.map { t ->
                CategorySpending(
                    categoryName = t.categoryName, colorHex = t.colorHex,
                    totalAmount = t.totalAmount,
                    percentage = if (total > 0) (t.totalAmount / total * 100).toFloat() else 0f
                )
            }
        }

    override fun getMonthlyStats(startDate: Long, endDate: Long): Flow<MonthlyStats> =
        transactionDao.getMonthlyStats(startDate, endDate).map { t ->
            MonthlyStats(totalIncome = t.totalIncome ?: 0.0, totalExpense = t.totalExpense ?: 0.0)
        }

    override fun getTodaySpending(startOfDay: Long, endOfDay: Long): Flow<Double> =
        transactionDao.getTodaySpending(startOfDay, endOfDay).map { it ?: 0.0 }

    override fun getRecentTransactions(limit: Int): Flow<List<Transaction>> =
        transactionDao.getRecentTransactions(limit).map { it.map { e -> e.toDomain() } }

    override fun getTransactionCount(): Flow<Int> = transactionDao.getTransactionCount()

    override fun getPendingAccountAssignments(): Flow<List<Transaction>> =
        transactionDao.getPendingAccountAssignments().map { it.map { e -> e.toDomain() } }

    override fun getPendingAssignmentCount(): Flow<Int> = transactionDao.getPendingAssignmentCount()

    override fun getTransactionsByAccountId(accountId: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsByAccountId(accountId).map { it.map { e -> e.toDomain() } }

    override suspend fun insert(transaction: Transaction): Long =
        transactionDao.insert(transaction.toEntity())

    override suspend fun update(transaction: Transaction) =
        transactionDao.update(transaction.toEntity())

    override suspend fun deleteById(id: Long) = transactionDao.deleteById(id)

    override suspend fun existsByHash(hash: String): Boolean = transactionDao.existsByHash(hash)

    override suspend fun assignAccount(transactionId: Long, accountId: Long) =
        transactionDao.assignAccount(transactionId, accountId)

    private suspend fun TransactionEntity.toDomain(): Transaction {
        val category = categoryId?.let { categoryDao.getCategoryById(it) }
        return Transaction(
            id = id, amount = amount, type = TransactionType.valueOf(type),
            categoryId = categoryId, categoryName = category?.name,
            categoryIcon = category?.iconName, categoryColor = category?.colorHex,
            description = description, accountNumber = accountNumber, bankName = bankName,
            transactionDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(transactionDate), ZoneId.systemDefault()),
            smsBody = smsBody, isManual = isManual, referenceId = referenceId,
            accountId = accountId, pendingAccountAssignment = pendingAccountAssignment
        )
    }

    private fun Transaction.toEntity(): TransactionEntity {
        return TransactionEntity(
            id = id, amount = amount, type = type.name, categoryId = categoryId,
            description = description, accountNumber = accountNumber, bankName = bankName,
            transactionDate = transactionDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            createdAt = System.currentTimeMillis(), smsBody = smsBody, isManual = isManual,
            referenceId = referenceId,
            smsHash = smsBody?.let {
                java.security.MessageDigest.getInstance("SHA-256")
                    .digest(it.trim().lowercase().toByteArray())
                    .joinToString("") { b -> "%02x".format(b) }
            },
            accountId = accountId, pendingAccountAssignment = pendingAccountAssignment
        )
    }
}
