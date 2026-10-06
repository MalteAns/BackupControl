package de.malteans.backup_control.commandExecution.presentation

import de.malteans.backup_control.commandExecution.domain.Command
import de.malteans.backup_control.core.presentation.util.UiText

data class CommandExecutionState(
    val isLoading: Boolean = true,
    val error: UiText? = null,

    val commands: List<Command> = emptyList(),
)
