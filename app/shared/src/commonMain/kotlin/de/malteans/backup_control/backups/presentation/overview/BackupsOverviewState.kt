package de.malteans.backup_control.backups.presentation.overview

import de.malteans.backup_control.backups.domain.Backup

data class BackupsOverviewState (
    val isLoading: Boolean = true,
    val backups: List<Backup> = emptyList(),
    val error: String? = null
)