package com.togai.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.togai.app.data.local.converter.Converters
import com.togai.app.data.local.dao.AccountDao
import com.togai.app.data.local.dao.BudgetDao
import com.togai.app.data.local.dao.CategoryDao
import com.togai.app.data.local.dao.SavingsGoalDao
import com.togai.app.data.local.dao.TransactionDao
import com.togai.app.data.local.entity.AccountEntity
import com.togai.app.data.local.entity.BudgetEntity
import com.togai.app.data.local.entity.CategoryEntity
import com.togai.app.data.local.entity.SavingsGoalEntity
import com.togai.app.data.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        SavingsGoalEntity::class,
        BudgetEntity::class,
        AccountEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun budgetDao(): BudgetDao
    abstract fun accountDao(): AccountDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `accounts` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `type` TEXT NOT NULL,
                `bank_name` TEXT,
                `account_number` TEXT,
                `balance` REAL NOT NULL DEFAULT 0.0,
                `credit_limit` REAL,
                `billing_cycle_day` INTEGER,
                `billing_due_date` INTEGER,
                `color_hex` TEXT NOT NULL DEFAULT '#6366F1',
                `is_default` INTEGER NOT NULL DEFAULT 0,
                `created_at` INTEGER NOT NULL DEFAULT 0
            )
        """)
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_accounts_account_number` ON `accounts` (`account_number`)")
        db.execSQL("ALTER TABLE `transactions` ADD COLUMN `account_id` INTEGER")
        db.execSQL("ALTER TABLE `transactions` ADD COLUMN `pending_account_assignment` INTEGER NOT NULL DEFAULT 0")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_account_id` ON `transactions` (`account_id`)")
    }
}

class DatabaseSeeder : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        val expenseCategories = listOf(
            Triple("Food & Dining", "Restaurant", "#FF6B35"),
            Triple("Transport", "DirectionsCar", "#3B82F6"),
            Triple("Shopping", "ShoppingCart", "#8B5CF6"),
            Triple("Bills & Utilities", "Receipt", "#EF4444"),
            Triple("Entertainment", "Movie", "#EC4899"),
            Triple("Health", "Favorite", "#10B981"),
            Triple("Education", "School", "#F59E0B"),
            Triple("ATM Withdrawal", "Money", "#6366F1"),
            Triple("UPI Transfer", "Send", "#14B8A6"),
            Triple("EMI / Loan", "AccountBalance", "#DC2626"),
            Triple("Investment", "TrendingUp", "#059669"),
            Triple("Groceries", "LocalGroceryStore", "#84CC16"),
            Triple("Rent", "Home", "#7C3AED"),
            Triple("Recharge", "PhoneAndroid", "#0EA5E9"),
            Triple("Other Expense", "MoreHoriz", "#6B7280"),
        )
        expenseCategories.forEach { (name, icon, color) ->
            db.execSQL(
                "INSERT OR IGNORE INTO categories (name, icon_name, color_hex, is_default, is_income) VALUES (?, ?, ?, 1, 0)",
                arrayOf(name, icon, color)
            )
        }
        val incomeCategories = listOf(
            Triple("Salary", "Wallet", "#22C55E"),
            Triple("Refund", "Replay", "#06B6D4"),
            Triple("Cashback", "CardGiftcard", "#A855F7"),
            Triple("Interest", "Percent", "#10B981"),
            Triple("Other Income", "AddCircle", "#4ADE80"),
        )
        incomeCategories.forEach { (name, icon, color) ->
            db.execSQL(
                "INSERT OR IGNORE INTO categories (name, icon_name, color_hex, is_default, is_income) VALUES (?, ?, ?, 1, 1)",
                arrayOf(name, icon, color)
            )
        }
    }
}
