package de.malteans.backup_control.backups.presentation.overview

sealed interface BackupsOverviewAction {
    data object LoadBackupsOverview : BackupsOverviewAction
    data class OpenBackupLog(val logFileName: String) : BackupsOverviewAction
}