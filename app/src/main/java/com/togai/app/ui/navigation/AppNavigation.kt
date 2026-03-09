package com.togai.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.togai.app.ui.screens.accounts.AccountsScreen
import com.togai.app.ui.screens.analytics.AnalyticsScreen
import com.togai.app.ui.screens.creditcards.CreditCardScreen
import com.togai.app.ui.screens.dashboard.DashboardScreen
import com.togai.app.ui.screens.savings.SavingsScreen
import com.togai.app.ui.screens.settings.SettingsScreen
import com.togai.app.ui.screens.transactions.AddTransactionScreen
import com.togai.app.ui.screens.transactions.TransactionsScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToTransactions = { navController.navigate(Screen.Transactions.route) }
            )
        }
        composable(Screen.Transactions.route) {
            TransactionsScreen(
                onAddClick = { navController.navigate(Screen.AddTransaction.route) }
            )
        }
        composable(Screen.Analytics.route) {
            AnalyticsScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateToSavings = { navController.navigate(Screen.Savings.route) },
                onNavigateToAccounts = { navController.navigate(Screen.Accounts.route) }
            )
        }
        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.Savings.route) {
            SavingsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.Accounts.route) {
            AccountsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreditCards = { navController.navigate(Screen.CreditCards.route) }
            )
        }
        composable(Screen.CreditCards.route) {
            CreditCardScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
