\
package com.togai.app.data.repository

import com.togai.app.data.local.dao.AccountDao
import com.togai.app.data.local.entity.AccountEntity
import com.togai.app.domain.model.Account
import com.togai.app.domain.model.AccountType
import com.togai.app.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
) : AccountRepository {

    override fun getAllAccounts(): Flow<List<Account>> =
        accountDao.getAllAccounts().map { list -> list.map { it.toDomain() } }

    override fun getAccountsByType(type: AccountType): Flow<List<Account>> =
        accountDao.getAccountsByType(type.name).map { list -> list.map { it.toDomain() } }

    override suspend fun getAccountById(id: Long): Account? =
        accountDao.getAccountById(id)?.toDomain()

    override suspend fun findByAccountNumber(accountNumber: String): Account? =
        accountDao.findByAccountNumber(accountNumber)?.toDomain()

    override suspend fun insert(account: Account): Long = accountDao.insert(account.toEntity())
    override suspend fun update(account: Account) = accountDao.update(account.toEntity())
    override suspend fun deleteById(id: Long) = accountDao.deleteById(id)
    override suspend fun updateBillingDueDate(accountId: Long, dueDate: Long) =
        accountDao.updateBillingDueDate(accountId, dueDate)

    private fun AccountEntity.toDomain() = Account(
        id = id, name = name, type = AccountType.valueOf(type),
        bankName = bankName, accountNumber = accountNumber, balance = balance,
        creditLimit = creditLimit, billingCycleDay = billingCycleDay,
        billingDueDate = billingDueDate, colorHex = colorHex, isDefault = isDefault
    )

    private fun Account.toEntity() = AccountEntity(
        id = id, name = name, type = type.name,
        bankName = bankName, accountNumber = accountNumber, balance = balance,
        creditLimit = creditLimit, billingCycleDay = billingCycleDay,
        billingDueDate = billingDueDate, colorHex = colorHex, isDefault = isDefault
    )
}
