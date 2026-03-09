package com.togai.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "accounts",
    indices = [Index("account_number")]
)
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String,
    @ColumnInfo(name = "bank_name")
    val bankName: String? = null,
    @ColumnInfo(name = "account_number")
    val accountNumber: String? = null,
    val balance: Double = 0.0,
    @ColumnInfo(name = "credit_limit")
    val creditLimit: Double? = null,
    @ColumnInfo(name = "billing_cycle_day")
    val billingCycleDay: Int? = null,
    @ColumnInfo(name = "billing_due_date")
    val billingDueDate: Long? = null,
    @ColumnInfo(name = "color_hex")
    val colorHex: String = "#6366F1",
    @ColumnInfo(name = "is_default")
    val isDefault: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
