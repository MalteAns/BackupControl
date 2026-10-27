package de.malteans.backup_control.settings.presentation

sealed interface SettingsAction {
    data object OnBack: SettingsAction

    data class ChangeServerUrl(val url: String): SettingsAction

    data class ChangeApiToken(val token: String): SettingsAction

    data object ToggleTokenVisibility: SettingsAction

    data object Save: SettingsAction

    data object TestConnection: SettingsAction
}
