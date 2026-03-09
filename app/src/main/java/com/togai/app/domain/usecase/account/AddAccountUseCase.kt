\
package com.togai.app.domain.usecase.account

import com.togai.app.domain.model.Account
import com.togai.app.domain.repository.AccountRepository
import javax.inject.Inject

class AddAccountUseCase @Inject constructor(private val repo: AccountRepository) {
    suspend operator fun invoke(account: Account): Long = repo.insert(account)
}
