package de.malteans.backup_control.backups.presentation.details

data class BackupDetailsState(
    val isLoading: Boolean = true,
    val error: String? = null,

    val logFileContent: String? = null,
)
