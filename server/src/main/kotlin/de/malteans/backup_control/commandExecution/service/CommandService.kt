package de.malteans.backup_control.commandExecution.service

import de.malteans.backup_control.commandExecution.db.CommandTable
import de.malteans.backup_control.commandExecution.domain.Command
import de.malteans.backup_control.commandExecution.domain.CommandConfig
import de.malteans.backup_control.commandExecution.util.writeToPipe
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface CommandService {
    fun updateCommands(commandConfigs: List<CommandConfig>): Result<Unit>

    fun getCommands(): Result<List<Command>>
    fun getCommand(uuid: String): Result<Command>

    fun executeCommand(uuid: String): Result<Unit>
}

@OptIn(ExperimentalUuidApi::class)
class CommandServiceImpl(
    private val database: Database,
) : CommandService {
    override fun updateCommands(commandConfigs: List<CommandConfig>): Result<Unit> = runCatching {
        transaction(database) {
            CommandTable.deleteAll()

            commandConfigs.forEach { config ->
                CommandTable.insert { insert ->
                    insert[CommandTable.uuid] = Uuid.generateV7().toHexDashString()
                    insert[CommandTable.name] = config.name
                    insert[CommandTable.command] = config.command
                }
            }
        }
    }

    override fun getCommands(): Result<List<Command>> = runCatching {
        transaction(database) {
            CommandTable
                .selectAll()
                .toCommandList()
        }
    }

    override fun getCommand(uuid: String): Result<Command> = runCatching {
        transaction(database) {
            CommandTable
                .selectAll()
                .where { CommandTable.uuid eq uuid }
                .toCommandList()
                .single()
        }
    }

    override fun executeCommand(uuid: String): Result<Unit> = runCatching {
        val command = transaction(database) {
            CommandTable
                .selectAll()
                .where { CommandTable.uuid eq uuid }
                .toCommandList()
                .single()
        }

        // TODO: get pipePath from commands.json
        writeToPipe(command.command, "/tmp/testPipe").getOrThrow()
    }

    private fun Iterable<ResultRow>.toCommandList(): List<Command> = this.map { row ->
        Command(
            uuid = row[CommandTable.uuid],
            name = row[CommandTable.name],
            command = row[CommandTable.command]
        )
    }
}