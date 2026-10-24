package de.malteans.backup_control.commandExecution.presentation

sealed interface CommandExecutionAction {
    data object OnBack: CommandExecutionAction

    data class ExecuteCommand(val uuid: String): CommandExecutionAction
}