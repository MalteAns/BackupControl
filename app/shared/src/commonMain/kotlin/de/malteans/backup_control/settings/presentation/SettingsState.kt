package de.malteans.backup_control.settings.presentation

data class SettingsState(
    val isLoading: Boolean = true,
    val serverUrl: String = "",
    val apiToken: String = "",
    val isTokenVisible: Boolean = false,
    val isServerUrlValid: Boolean = true,
    val isSaving: Boolean = false,
    val isTestRunning: Boolean = false,
    val isTestSuccessful: Boolean? = null,
)
