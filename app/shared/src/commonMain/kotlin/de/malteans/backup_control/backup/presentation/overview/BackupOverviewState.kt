package de.malteans.backup_control.backup.presentation.overview

import de.malteans.backup_control.backup.domain.Backup

data class BackupOverviewState (
    val isLoading: Boolean = true,
    val backups: List<Backup> = emptyList(),
    val error: String? = null
)