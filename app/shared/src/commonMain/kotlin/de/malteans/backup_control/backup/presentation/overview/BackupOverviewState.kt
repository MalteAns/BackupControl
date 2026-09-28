package de.malteans.backup_control.backup.presentation.overview

import de.malteans.backup_control.model.Backup

data class BackupOverviewState (
    val isLoading: Boolean = true,
    val backups: List<Backup> = emptyList(),
    val error: String? = null
)