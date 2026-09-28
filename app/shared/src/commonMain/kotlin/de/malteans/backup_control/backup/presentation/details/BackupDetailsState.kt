package de.malteans.backup_control.backup.presentation.details

import de.malteans.backup_control.model.Backup

data class BackupDetailsState(
    val isLoading: Boolean = true,
    val error: String? = null,

    val selectedBackup: Backup? = null,
    val logFileContent: String? = null,
)
