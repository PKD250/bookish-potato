package com.togai.app.ui.screens.creditcards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.togai.app.domain.model.Account
import com.togai.app.ui.common.LoadingIndicator
import com.togai.app.util.formatCurrency
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditCardScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreditCardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Credit Cards") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            LoadingIndicator(modifier = Modifier.fillMaxSize())
            return@Scaffold
        }

        if (state.cards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No credit cards added",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Add credit cards via Settings \u2192 Manage Accounts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(Modifier.height(4.dp)) }

            item {
                val totalSpent = state.cards.sumOf { it.totalSpent }
                val totalLimit = state.cards.sumOf { it.account.creditLimit ?: 0.0 }
                val overallUtil = if (totalLimit > 0) (totalSpent / totalLimit * 100).toFloat() else 0f

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Overall Utilization",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { overallUtil / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            color = utilizationColor(overallUtil)
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = formatCurrency(totalSpent),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "${overallUtil.toInt()}% of ${formatCurrency(totalLimit)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            items(state.cards) { summary ->
                CreditCardItem(summary = summary)
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun CreditCardItem(summary: CreditCardSummary) {
    val account = summary.account
    val limit = account.creditLimit ?: 0.0
    val available = (limit - summary.totalSpent).coerceAtLeast(0.0)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(12.dp),
                    shape = MaterialTheme.shapes.extraSmall,
                    color = parseHexColor(account.colorHex)
                ) {}
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (account.bankName != null) {
                        Text(
                            text = account.bankName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = "${summary.utilizationPercent.toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = utilizationColor(summary.utilizationPercent)
                )
            }

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { summary.utilizationPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                color = utilizationColor(summary.utilizationPercent)
            )

            Spacer(Modifier.height(10.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AmountColumn(label = "Spent", amount = summary.totalSpent, alignment = Alignment.Start)
                AmountColumn(label = "Available", amount = available, alignment = Alignment.CenterHorizontally)
                AmountColumn(label = "Limit", amount = limit, alignment = Alignment.End)
            }

            if (summary.billingDueDate != null) {
                Spacer(Modifier.height(8.dp))
                val formatted = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(Date(summary.billingDueDate))
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = "Due: $formatted",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun AmountColumn(
    label: String,
    amount: Double,
    alignment: Alignment.Horizontal
) {
    Column(horizontalAlignment = alignment) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = formatCurrency(amount),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun utilizationColor(percent: Float): Color = when {
    percent >= 80f -> MaterialTheme.colorScheme.error
    percent >= 50f -> MaterialTheme.colorScheme.tertiary
    else -> MaterialTheme.colorScheme.primary
}

private fun parseHexColor(hex: String?): Color = try {
    Color(android.graphics.Color.parseColor(hex ?: "#6366F1"))
} catch (_: Exception) {
    Color(0xFF6366F1)
}
