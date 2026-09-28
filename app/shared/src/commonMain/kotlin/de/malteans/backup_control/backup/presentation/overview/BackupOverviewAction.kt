package de.malteans.backup_control.backup.presentation.overview

sealed interface BackupOverviewAction {
    data object LoadBackupOverview : BackupOverviewAction
    data class OpenBackupLog(val logFileName: String) : BackupOverviewAction
}