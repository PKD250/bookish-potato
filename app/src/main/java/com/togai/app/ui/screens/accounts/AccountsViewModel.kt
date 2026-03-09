package com.togai.app.ui.screens.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.togai.app.domain.model.Account
import com.togai.app.domain.model.AccountType
import com.togai.app.domain.usecase.account.AddAccountUseCase
import com.togai.app.domain.usecase.account.DeleteAccountUseCase
import com.togai.app.domain.usecase.account.GetAccountsUseCase
import com.togai.app.domain.usecase.account.UpdateAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountsUiState(
    val accountsByType: Map<AccountType, List<Account>> = emptyMap(),
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val addAccountUseCase: AddAccountUseCase,
    private val updateAccountUseCase: UpdateAccountUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountsUiState())
    val uiState: StateFlow<AccountsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getAccountsUseCase().collect { accounts ->
                _uiState.value = _uiState.value.copy(
                    accountsByType = accounts.groupBy { it.type },
                    isLoading = false
                )
            }
        }
    }

    fun showAddDialog() { _uiState.value = _uiState.value.copy(showAddDialog = true) }
    fun hideAddDialog() { _uiState.value = _uiState.value.copy(showAddDialog = false) }

    fun addAccount(
        name: String, type: AccountType, bankName: String, accountNumber: String,
        balance: Double, creditLimit: Double?, billingCycleDay: Int?, colorHex: String
    ) {
        viewModelScope.launch {
            runCatching {
                addAccountUseCase(Account(
                    name = name.trim(), type = type,
                    bankName = bankName.trim().takeIf { it.isNotEmpty() },
                    accountNumber = accountNumber.trim().takeIf { it.isNotEmpty() },
                    balance = balance, creditLimit = creditLimit,
                    billingCycleDay = billingCycleDay, colorHex = colorHex
                ))
                _uiState.value = _uiState.value.copy(showAddDialog = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteAccount(id: Long) {
        viewModelScope.launch {
            runCatching { deleteAccountUseCase(id) }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
        }
    }
}
