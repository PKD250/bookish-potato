package com.togai.app.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.togai.app.domain.model.Category
import com.togai.app.domain.model.Transaction
import com.togai.app.domain.model.TransactionType
import com.togai.app.domain.usecase.category.GetCategoriesUseCase
import com.togai.app.domain.usecase.transaction.DeleteTransactionUseCase
import com.togai.app.domain.usecase.transaction.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionsUiState(
    val transactions: List<Transaction> = emptyList(),
    val filteredTransactions: List<Transaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val filterType: TransactionType? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                getTransactionsUseCase(),
                getCategoriesUseCase()
            ) { transactions, categories ->
                val filtered = applyFilters(transactions, _uiState.value.filterType, _uiState.value.searchQuery)
                TransactionsUiState(
                    transactions = transactions,
                    filteredTransactions = filtered,
                    categories = categories,
                    filterType = _uiState.value.filterType,
                    searchQuery = _uiState.value.searchQuery,
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }

    fun setFilter(type: TransactionType?) {
        val current = _uiState.value
        val filtered = applyFilters(current.transactions, type, current.searchQuery)
        _uiState.value = current.copy(filterType = type, filteredTransactions = filtered)
    }

    fun setSearchQuery(query: String) {
        val current = _uiState.value
        val filtered = applyFilters(current.transactions, current.filterType, query)
        _uiState.value = current.copy(searchQuery = query, filteredTransactions = filtered)
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            deleteTransactionUseCase(id)
        }
    }

    private fun applyFilters(
        transactions: List<Transaction>,
        type: TransactionType?,
        query: String
    ): List<Transaction> {
        return transactions
            .filter { type == null || it.type == type }
            .filter {
                query.isBlank() ||
                    it.description.contains(query, ignoreCase = true) ||
                    (it.bankName?.contains(query, ignoreCase = true) == true) ||
                    (it.categoryName?.contains(query, ignoreCase = true) == true)
            }
    }
}
