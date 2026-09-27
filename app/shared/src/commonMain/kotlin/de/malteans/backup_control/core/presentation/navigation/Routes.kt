package de.malteans.backup_control.core.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Backup : Route {
        @Serializable
        data object Overview : Route
        @Serializable
        data  class Details(val logFileName: String) : Route
    }
}
