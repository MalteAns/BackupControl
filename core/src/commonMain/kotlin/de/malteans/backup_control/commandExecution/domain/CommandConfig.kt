package de.malteans.backup_control.commandExecution.domain

import kotlinx.serialization.Serializable

@Serializable
data class CommandConfig(
    val name: String?,
    val command: String,
)

@Serializable
data class CommandsConfig(
    val pipePath: String,
    val commands: List<CommandConfig>,
)
