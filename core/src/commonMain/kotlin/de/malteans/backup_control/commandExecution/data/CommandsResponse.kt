package de.malteans.backup_control.commandExecution.data

import de.malteans.backup_control.commandExecution.domain.Command
import kotlinx.serialization.Serializable

@Serializable
data class CommandsResponse(
    val commands: List<Command>,
)
