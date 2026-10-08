package de.malteans.backup_control.commandExecution.presentation

import androidx.compose.material3.SnackbarDuration
import de.malteans.backup_control.core.presentation.util.UiText

sealed interface CommandExecutionEvent {
    data class ShowSnackbar(
        val message: UiText,
        val actionLabel: String? = null,
        val duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
        val withDismissAction: Boolean = duration == SnackbarDuration.Indefinite,
        val onAction: () -> Unit = {},
    ): CommandExecutionEvent
}