\
package com.togai.app.domain.usecase.account

import com.togai.app.domain.model.Account
import com.togai.app.domain.model.AccountType
import com.togai.app.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccountsUseCase @Inject constructor(private val repo: AccountRepository) {
    operator fun invoke(): Flow<List<Account>> = repo.getAllAccounts()
    fun byType(type: AccountType): Flow<List<Account>> = repo.getAccountsByType(type)
}
