package com.togai.app.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.togai.app.domain.model.TransactionType
import com.togai.app.ui.theme.ExpenseRed
import com.togai.app.ui.theme.IncomeGreen
import com.togai.app.util.formatCurrency

@Composable
fun AmountText(
    amount: Double,
    type: TransactionType,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    showSign: Boolean = true
) {
    val color = when (type) {
        TransactionType.DEBIT -> ExpenseRed
        TransactionType.CREDIT -> IncomeGreen
    }
    val prefix = when {
        !showSign -> ""
        type == TransactionType.DEBIT -> "- "
        else -> "+ "
    }

    Text(
        text = "$prefix${amount.formatCurrency()}",
        color = color,
        style = style,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )
}
