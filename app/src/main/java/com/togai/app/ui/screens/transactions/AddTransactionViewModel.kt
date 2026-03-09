package com.togai.app.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.togai.app.domain.model.Category
import com.togai.app.domain.model.Transaction
import com.togai.app.domain.model.TransactionType
import com.togai.app.domain.usecase.category.GetCategoriesUseCase
import com.togai.app.domain.usecase.transaction.AddTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

data class AddTransactionUiState(
    val amount: String = "",
    val description: String = "",
    val type: TransactionType = TransactionType.DEBIT,
    val selectedCategory: Category? = null,
    val categories: List<Category> = emptyList(),
    val date: LocalDateTime = LocalDateTime.now(),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getCategoriesUseCase().collect { categories ->
                _uiState.value = _uiState.value.copy(categories = categories)
            }
        }
    }

    fun setAmount(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount, error = null)
    }

    fun setDescription(desc: String) {
        _uiState.value = _uiState.value.copy(description = desc)
    }

    fun setType(type: TransactionType) {
        _uiState.value = _uiState.value.copy(type = type, selectedCategory = null)
    }

    fun setCategory(category: Category) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun setDate(date: LocalDateTime) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun save() {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            _uiState.value = state.copy(error = "Enter a valid amount")
            return
        }
        if (state.description.isBlank()) {
            _uiState.value = state.copy(error = "Enter a description")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true)
            try {
                addTransactionUseCase(
                    Transaction(
                        amount = amount,
                        type = state.type,
                        categoryId = state.selectedCategory?.id,
                        categoryName = state.selectedCategory?.name,
                        description = state.description,
                        transactionDate = state.date,
                        isManual = true
                    )
                )
                _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
            }
        }
    }
}
