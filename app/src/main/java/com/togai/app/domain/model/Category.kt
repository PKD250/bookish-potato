package com.togai.app.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val isDefault: Boolean = true,
    val isIncome: Boolean = false
)
