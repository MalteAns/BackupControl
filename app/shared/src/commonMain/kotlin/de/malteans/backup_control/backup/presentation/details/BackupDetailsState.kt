package de.malteans.backup_control.backup.presentation.details

data class BackupDetailsState(
    val isLoading: Boolean = true,
    val error: String? = null,

    val logFileContent: String? = null,
)
