package de.malteans.backup_control.backups.presentation

sealed interface BackupsAction {
    data object LoadBackups : BackupsAction
    data class OpenBackupLog(val backupId: String) : BackupsAction
}