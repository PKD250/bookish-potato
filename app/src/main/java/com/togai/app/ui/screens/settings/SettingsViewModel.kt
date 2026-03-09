package com.togai.app.ui.screens.settings

import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.togai.app.data.preferences.SyncPreferencesManager
import com.togai.app.domain.repository.ImportProgress
import com.togai.app.domain.usecase.export.ExportToCsvUseCase
import com.togai.app.domain.usecase.sms.ImportHistoricalSmsUseCase
import com.togai.app.domain.usecase.transaction.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val hasSmsPermission: Boolean = false,
    val isImporting: Boolean = false,
    val importProgress: ImportProgress? = null,
    val isExporting: Boolean = false,
    val exportSuccess: Boolean = false,
    val transactionCount: Int = 0,
    val lastSyncTimestamp: Long = 0L,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val application: Application,
    private val importHistoricalSmsUseCase: ImportHistoricalSmsUseCase,
    private val exportToCsvUseCase: ExportToCsvUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val syncPreferencesManager: SyncPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        checkPermissions()
        _uiState.value = _uiState.value.copy(lastSyncTimestamp = syncPreferencesManager.lastSyncTimestamp)
        viewModelScope.launch {
            getTransactionsUseCase().collect { transactions ->
                _uiState.value = _uiState.value.copy(transactionCount = transactions.size)
            }
        }
    }

    fun checkPermissions() {
        val hasPermission = ContextCompat.checkSelfPermission(
            application, android.Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
        _uiState.value = _uiState.value.copy(hasSmsPermission = hasPermission)
    }

    fun importSms() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isImporting = true, error = null)
            try {
                importHistoricalSmsUseCase.invoke().collect { progress ->
                    _uiState.value = _uiState.value.copy(importProgress = progress)
                }
                _uiState.value = _uiState.value.copy(
                    isImporting = false,
                    lastSyncTimestamp = syncPreferencesManager.lastSyncTimestamp
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isImporting = false, error = e.message)
            }
        }
    }

    fun exportCsv() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true, error = null)
            try {
                val transactions = getTransactionsUseCase().first()
                exportToCsvUseCase(transactions)
                _uiState.value = _uiState.value.copy(isExporting = false, exportSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isExporting = false, error = e.message)
            }
        }
    }
}
