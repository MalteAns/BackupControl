package de.malteans.backup_control.commandExecution.service

import de.malteans.backup_control.commandExecution.domain.CommandConfig
import de.malteans.backup_control.commandExecution.domain.CommandsConfig
import kotlinx.serialization.json.Json
import java.io.File

object CommandConfigLoader {

    private val json = Json { ignoreUnknownKeys = true }

    fun loadFromFile(path: String): Result<List<CommandConfig>> = runCatching {
        val file = File(path)

        require(file.exists()) { "File does not exist: $path" }
        require(file.isFile) { "File is not a file: $path" }

        val content = file.readText()
        val commandsConfig = json.decodeFromString<CommandsConfig>(content)

        validateCommandsConfig(commandsConfig).getOrThrow()

        commandsConfig.commands
    }

    private fun validateCommandsConfig(config: CommandsConfig): Result<CommandsConfig> = runCatching {
        config.copy(
            commands = config.commands.map { commandConfig ->
                validateCommandConfig(commandConfig).getOrThrow()
            }
        )
    }

    private fun validateCommandConfig(config: CommandConfig): Result<CommandConfig> = runCatching {
        require(config.command.isNotBlank()) { "Command name must not be blank" }
        config
    }
}
