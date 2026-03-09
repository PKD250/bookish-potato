package com.togai.app.domain.usecase.transaction

import com.togai.app.domain.model.Transaction
import com.togai.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<List<Transaction>> = repository.getAllTransactions()

    fun byType(type: String): Flow<List<Transaction>> = repository.getTransactionsByType(type)

    fun search(query: String): Flow<List<Transaction>> = repository.searchTransactions(query)

    fun recent(limit: Int = 10): Flow<List<Transaction>> = repository.getRecentTransactions(limit)
}
