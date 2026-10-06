package de.malteans.backup_control.commandExecution.presentation

import de.malteans.backup_control.core.presentation.util.UiText

sealed interface CommandExecutionEvent {
    data class ShowSnackbar(val message: UiText): CommandExecutionEvent
}