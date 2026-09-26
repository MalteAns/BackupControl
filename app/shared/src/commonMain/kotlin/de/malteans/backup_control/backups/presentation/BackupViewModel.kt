package de.malteans.backup_control.backups.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.backup_control.backups.domain.BackupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BackupViewModel(
    private val repository: BackupRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BackupsState())
    val state = _state
        .stateIn(
        viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            BackupsState()
        )

    init {
        loadBackups()
    }

    fun onAction(action: BackupsAction) {
        when (action) {
            is BackupsAction.LoadBackups -> loadBackups()
            is BackupsAction.OpenBackupLog -> throw IllegalArgumentException("$action should be handled in RootScreen")
        }
    }

    private fun loadBackups() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = repository.getBackups()
            _state.update { it.copy(
                isLoading = false,
                backups = result.getOrNull() ?: emptyList(),
                error = result.exceptionOrNull()?.message
            ) }
        }
    }
}
