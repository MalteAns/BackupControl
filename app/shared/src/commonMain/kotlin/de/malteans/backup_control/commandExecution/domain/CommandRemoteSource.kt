package de.malteans.backup_control.commandExecution.domain

interface CommandRemoteSource {
    suspend fun getCommands(): Result<List<Command>>
    suspend fun executeCommand(uuid: String): Result<Unit>
}