\
package com.togai.app.domain.usecase.account

import com.togai.app.domain.repository.AccountRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(private val repo: AccountRepository) {
    suspend operator fun invoke(id: Long) = repo.deleteById(id)
}
