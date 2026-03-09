package com.togai.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    data object Dashboard : Screen("dashboard", "Home", Icons.Filled.Home)
    data object Transactions : Screen("transactions", "Transactions", Icons.Filled.Receipt)
    data object Analytics : Screen("analytics", "Analytics", Icons.Filled.BarChart)
    data object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
    data object AddTransaction : Screen("add_transaction", "Add Transaction")
    data object Savings : Screen("savings", "Savings")
    data object Accounts : Screen("accounts", "Accounts")
    data object CreditCards : Screen("credit_cards", "Credit Cards")

    companion object {
        val bottomNavItems = listOf(Dashboard, Transactions, Analytics, Settings)
    }
}
