\
package com.togai.app.domain.repository

import com.togai.app.domain.model.Account
import com.togai.app.domain.model.AccountType
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAllAccounts(): Flow<List<Account>>
    fun getAccountsByType(type: AccountType): Flow<List<Account>>
    suspend fun getAccountById(id: Long): Account?
    suspend fun findByAccountNumber(accountNumber: String): Account?
    suspend fun insert(account: Account): Long
    suspend fun update(account: Account)
    suspend fun deleteById(id: Long)
    suspend fun updateBillingDueDate(accountId: Long, dueDate: Long)
}
