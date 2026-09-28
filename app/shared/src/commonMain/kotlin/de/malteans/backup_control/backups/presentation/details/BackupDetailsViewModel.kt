package de.malteans.backup_control.backups.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.backup_control.backups.domain.BackupRemoteSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BackupDetailsViewModel(
    private val logFileName: String,
    private val backupRemoteSource: BackupRemoteSource,
): ViewModel() {

    private val _state = MutableStateFlow(BackupDetailsState())

    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            BackupDetailsState()
        )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            backupRemoteSource.getBackupLogFile(logFileName)
                .onSuccess { logFileContent ->
                    _state.update { it.copy(
                        isLoading = false,
                        error = null,
                        logFileContent = logFileContent,
                    ) }
                }
                .onFailure { error ->
                    _state.update { it.copy(
                        isLoading = false,
                        error = error.message,
                        logFileContent = null,
                    ) }
                }

        }
    }

    fun onAction(action: BackupDetailsAction) {
        when (action) {

            else -> throw IllegalArgumentException("Unknown action: $action")
        }
    }
}