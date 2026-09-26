package de.malteans.backup_control.backups.presentation

import de.malteans.backup_control.backups.domain.Backup

data class BackupsState (
    val isLoading: Boolean = true,
    val backups: List<Backup> = emptyList(),
    val error: String? = null
)