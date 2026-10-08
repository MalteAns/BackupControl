package de.malteans.backup_control.commandExecution.service

import de.malteans.backup_control.commandExecution.domain.CommandConfig
import de.malteans.backup_control.commandExecution.domain.CommandsConfig
import kotlinx.serialization.json.Json
import java.io.File

object CommandConfigLoader {

    private val json = Json { ignoreUnknownKeys = true }

    fun loadFromFile(path: String): Result<CommandsConfig> = runCatching {
        val file = File(path)

        require(file.exists()) { "File does not exist: $path" }
        require(file.isFile) { "File is not a file: $path" }

        val content = file.readText()
        val commandsConfig = json.decodeFromString<CommandsConfig>(content)

        validateCommandsConfig(commandsConfig).getOrThrow()

        commandsConfig
    }

    private fun validateCommandsConfig(config: CommandsConfig): Result<Unit> = runCatching {
        validatePipePath(config.pipePath).getOrThrow()
        config.commands.forEach { command ->
            validateCommand(command).getOrThrow()
        }
    }

    private fun validatePipePath(pipePath: String): Result<Unit> = runCatching {
        require(pipePath.isNotBlank()) { "Pipe path cannot be blank" }
        // TODO: Add more
    }

    private fun validateCommand(command: CommandConfig): Result<Unit> = runCatching {
        require(command.name?.isNotBlank() == true) { "Command name can't be blank" }
        require(command.command.isNotBlank()) { "Command can't be blank" }
        // TODO: Add more
    }
}
