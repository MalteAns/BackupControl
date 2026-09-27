package de.malteans.backup_control.core.presentation.navigation

sealed interface AppRoute

data object BackupsRoute : AppRoute

data class BackupDetailRoute(val logFileName: String) : AppRoute
