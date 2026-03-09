package com.togai.app.di

import com.togai.app.data.repository.AccountRepositoryImpl
import com.togai.app.data.repository.BudgetRepositoryImpl
import com.togai.app.data.repository.CategoryRepositoryImpl
import com.togai.app.data.repository.SavingsGoalRepositoryImpl
import com.togai.app.data.repository.SmsRepositoryImpl
import com.togai.app.data.repository.TransactionRepositoryImpl
import com.togai.app.domain.repository.AccountRepository
import com.togai.app.domain.repository.BudgetRepository
import com.togai.app.domain.repository.CategoryRepository
import com.togai.app.domain.repository.SavingsGoalRepository
import com.togai.app.domain.repository.SmsRepository
import com.togai.app.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository

    @Binds @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds @Singleton
    abstract fun bindSavingsGoalRepository(impl: SavingsGoalRepositoryImpl): SavingsGoalRepository

    @Binds @Singleton
    abstract fun bindBudgetRepository(impl: BudgetRepositoryImpl): BudgetRepository

    @Binds @Singleton
    abstract fun bindSmsRepository(impl: SmsRepositoryImpl): SmsRepository

    @Binds @Singleton
    abstract fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository
}
