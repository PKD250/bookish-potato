package com.togai.app.domain.usecase.account

import com.togai.app.domain.model.Account
import com.togai.app.domain.repository.AccountRepository
import javax.inject.Inject

class UpdateAccountUseCase @Inject constructor(private val repo: AccountRepository) {
    suspend operator fun invoke(account: Account) = repo.update(account)
}
