package com.togai.app.di

import android.content.Context
import androidx.room.Room
import com.togai.app.data.local.AppDatabase
import com.togai.app.data.local.DatabaseSeeder
import com.togai.app.data.local.MIGRATION_1_2
import com.togai.app.data.local.dao.AccountDao
import com.togai.app.data.local.dao.BudgetDao
import com.togai.app.data.local.dao.CategoryDao
import com.togai.app.data.local.dao.SavingsGoalDao
import com.togai.app.data.local.dao.TransactionDao
import com.togai.app.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        )
            .addCallback(DatabaseSeeder())
            .addMigrations(MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideSavingsGoalDao(db: AppDatabase): SavingsGoalDao = db.savingsGoalDao()

    @Provides
    fun provideBudgetDao(db: AppDatabase): BudgetDao = db.budgetDao()

    @Provides
    fun provideAccountDao(db: AppDatabase): AccountDao = db.accountDao()
}
