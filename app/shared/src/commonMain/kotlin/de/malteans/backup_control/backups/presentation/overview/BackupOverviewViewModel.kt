package de.malteans.backup_control.backups.presentation.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.backup_control.backups.domain.BackupRemoteSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BackupOverviewViewModel(
    private val repository: BackupRemoteSource
) : ViewModel() {

    private val _state = MutableStateFlow(BackupsOverviewState())
    val state = _state
        .stateIn(
        viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            BackupsOverviewState()
        )

    init {
        loadBackups()
    }

    fun onAction(action: BackupsOverviewAction) {
        when (action) {
            is BackupsOverviewAction.LoadBackupsOverview -> loadBackups()
            is BackupsOverviewAction.OpenBackupLog -> throw IllegalArgumentException("$action should be handled in RootScreen")
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
