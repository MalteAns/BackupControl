package de.malteans.backup_control.backup.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.backup_control.backup.domain.BackupRemoteSource
import de.malteans.backup_control.backup.presentation.components.formatByteSize
import de.malteans.backup_control.backup.presentation.components.formatDuration
import de.malteans.backup_control.backup.presentation.details.components.FileDistributionUi
import de.malteans.backup_control.backup.presentation.details.components.toUiModel
import de.malteans.backup_control.core.presentation.util.UiText
import de.malteans.backup_control.model.Backup
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
            loadBackup()
        }
    }

    fun onAction(action: BackupDetailsAction) {
        when (action) {

            else -> throw IllegalArgumentException("Unknown action: $action")
        }
    }

    private suspend fun loadBackup() {
        backupRemoteSource.getBackups()
            .onSuccess { backups ->
                val backup = backups.find { backup -> backup.fileName == logFileName }
                if (backup != null) setStateForBackup(backup)
                _state.update { it.copy(
                    isLoading = false,
                    error = null,
                ) }
            }
            .onFailure { error ->
                _state.update {
                    BackupDetailsState(
                        isLoading = false,
                        error = error.message ?: "Unknown error",
                    )
                }
            }
//        backupRemoteSource.getBackupLogFile(logFileName)
//            .onSuccess { logFileContent ->
//                _state.update { it.copy(
//                    isLoading = false,
//                    error = null,
//                    logFileContent = logFileContent,
//                ) }
//            }
//            .onFailure { error ->
//                _state.update { it.copy(
//                    isLoading = false,
//                    error = error.message,
//                    logFileContent = null,
//                ) }
//            }
    }

    private fun setStateForBackup(backup: Backup) {
        _state.update { state -> state.copy(
            selectedBackup = backup,

            startTime = backup.startTime.toString(),
            fileName = backup.fileName,
            success = backup.success,
            duration = backup.duration?.formatDuration() ?: UiText.DynamicString("—"),
            totalFileSize = backup.totalFileSize?.formatByteSize() ?: "—",

            totalFiles = backup.totalFiles?.toUiModel() ?: FileDistributionUi(),
            createdFiles = backup.createdFiles?.toUiModel() ?: FileDistributionUi(),
            deletedFiles = backup.deletedFiles?.toUiModel() ?: FileDistributionUi(),

            transferredRegularFiles = backup.transferredRegularFiles?.toString() ?: "—",
            transferredFileSize = backup.transferredFileSize?.formatByteSize() ?: "—",
            totalBytesSent = backup.totalBytesSent?.formatByteSize() ?: "—",
            totalBytesReceived = backup.totalBytesReceived?.formatByteSize() ?: "—",
            bytesPerSecond = backup.bytesPerSecond?.let { "${it.toLong().formatByteSize()}/s" } ?: "—",
            speedup = backup.speedup?.toString() ?: "—",
        ) }
    }
}