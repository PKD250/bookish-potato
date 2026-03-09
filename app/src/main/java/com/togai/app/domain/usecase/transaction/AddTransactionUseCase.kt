package com.togai.app.domain.usecase.transaction

import com.togai.app.domain.model.Transaction
import com.togai.app.domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): Long {
        return repository.insert(transaction)
    }
}
