package com.togai.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("transaction_date"),
        Index("type"),
        Index("category_id"),
        Index("sms_hash", unique = true),
        Index("account_id")
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: String,
    @ColumnInfo(name = "category_id")
    val categoryId: Long? = null,
    val description: String,
    @ColumnInfo(name = "account_number")
    val accountNumber: String? = null,
    @ColumnInfo(name = "bank_name")
    val bankName: String? = null,
    @ColumnInfo(name = "transaction_date")
    val transactionDate: Long,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "sms_body")
    val smsBody: String? = null,
    @ColumnInfo(name = "is_manual")
    val isManual: Boolean = false,
    @ColumnInfo(name = "reference_id")
    val referenceId: String? = null,
    @ColumnInfo(name = "sms_hash")
    val smsHash: String? = null,
    @ColumnInfo(name = "account_id")
    val accountId: Long? = null,
    @ColumnInfo(name = "pending_account_assignment")
    val pendingAccountAssignment: Boolean = false
)
