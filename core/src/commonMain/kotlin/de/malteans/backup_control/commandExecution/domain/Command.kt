package de.malteans.backup_control.commandExecution.domain

import kotlinx.serialization.Serializable

@Serializable
data class Command(
    val uuid: String,
    val name: String?,
    val command: String,
)
