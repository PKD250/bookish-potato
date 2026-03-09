package com.togai.app.ui.screens.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.togai.app.ui.common.LoadingIndicator
import com.togai.app.ui.screens.dashboard.components.SpendingOverviewChart
import com.togai.app.ui.theme.ExpenseRed
import com.togai.app.ui.theme.IncomeGreen
import com.togai.app.util.formatCurrency
import com.togai.app.util.hexToColor
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Analytics",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Month selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.previousMonth() }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Previous month")
            }
            Text(
                text = state.selectedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(
                onClick = { viewModel.nextMonth() },
                enabled = state.selectedMonth.isBefore(YearMonth.now())
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Next month")
            }
        }

        if (state.isLoading) {
            LoadingIndicator()
            return
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Income vs Expense summary
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Income vs Expense", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Income", color = IncomeGreen, fontWeight = FontWeight.Medium)
                    Text(state.stats.totalIncome.formatCurrency(), color = IncomeGreen, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = {
                        val max = maxOf(state.stats.totalIncome, state.stats.totalExpense)
                        if (max > 0) (state.stats.totalIncome / max).toFloat() else 0f
                    },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = IncomeGreen,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Expense", color = ExpenseRed, fontWeight = FontWeight.Medium)
                    Text(state.stats.totalExpense.formatCurrency(), color = ExpenseRed, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = {
                        val max = maxOf(state.stats.totalIncome, state.stats.totalExpense)
                        if (max > 0) (state.stats.totalExpense / max).toFloat() else 0f
                    },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = ExpenseRed,
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Net Savings", fontWeight = FontWeight.Medium)
                    Text(
                        state.stats.netSavings.formatCurrency(),
                        fontWeight = FontWeight.Bold,
                        color = if (state.stats.netSavings >= 0) IncomeGreen else ExpenseRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category breakdown
        if (state.categorySpending.isNotEmpty()) {
            SpendingOverviewChart(categorySpending = state.categorySpending)

            Spacer(modifier = Modifier.height(16.dp))

            // Top categories list
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Top Spending Categories", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))

                    state.categorySpending.forEachIndexed { index, spending ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(spending.categoryName, style = MaterialTheme.typography.bodyMedium)
                                LinearProgressIndicator(
                                    progress = { spending.percentage / 100f },
                                    modifier = Modifier.fillMaxWidth().height(4.dp),
                                    color = spending.colorHex.hexToColor(),
                                )
                            }
                            Text(
                                text = spending.totalAmount.formatCurrency(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
