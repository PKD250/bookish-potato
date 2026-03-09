package com.togai.app.ui.screens.creditcards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.togai.app.domain.model.Account
import com.togai.app.domain.model.AccountType
import com.togai.app.domain.repository.TransactionRepository
import com.togai.app.domain.usecase.account.GetAccountsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

data class CreditCardSummary(
    val account: Account,
    val totalSpent: Double,
    val utilizationPercent: Float,
    val billingDueDate: Long?
)

data class CreditCardUiState(
    val cards: List<CreditCardSummary> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CreditCardViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreditCardUiState())
    val uiState: StateFlow<CreditCardUiState> = _uiState.asStateFlow()

    init {
        loadCreditCards()
    }

    private fun loadCreditCards() {
        viewModelScope.launch {
            combine(
                getAccountsUseCase.byType(AccountType.CREDIT_CARD),
                transactionRepository.getAllTransactions()
            ) { accounts, allTransactions ->
                val now = LocalDate.now()
                accounts.map { account ->
                    val billingStart = if (account.billingCycleDay != null) {
                        val day = account.billingCycleDay
                        val thisMonthStart = now.withDayOfMonth(minOf(day, now.lengthOfMonth()))
                        if (now.isBefore(thisMonthStart)) thisMonthStart.minusMonths(1)
                        else thisMonthStart
                    } else {
                        YearMonth.now().atDay(1)
                    }
                    val startEpoch = billingStart
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant().toEpochMilli()

                    val spent = allTransactions
                        .filter { t ->
                            t.accountId == account.id &&
                            t.type.name == "DEBIT" &&
                            t.transactionDate
                                .atZone(ZoneId.systemDefault())
                                .toInstant().toEpochMilli() >= startEpoch
                        }
                        .sumOf { it.amount }

                    val util = if ((account.creditLimit ?: 0.0) > 0) {
                        (spent / account.creditLimit!! * 100).toFloat().coerceIn(0f, 100f)
                    } else 0f

                    CreditCardSummary(
                        account = account,
                        totalSpent = spent,
                        utilizationPercent = util,
                        billingDueDate = account.billingDueDate
                    )
                }
            }.collect { cards ->
                _uiState.value = CreditCardUiState(cards = cards, isLoading = false)
            }
        }
    }
}
