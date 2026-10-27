package de.malteans.backup_control.commandExecution.data

import de.malteans.backup_control.Endpoints
import de.malteans.backup_control.commandExecution.domain.Command
import de.malteans.backup_control.commandExecution.domain.CommandRemoteSource
import de.malteans.backup_control.core.data.network.safeCall
import de.malteans.backup_control.core.data.settings.ServerConfigRepository
import de.malteans.backup_control.core.data.settings.ServerNotConfiguredException
import io.ktor.client.*
import io.ktor.client.request.*

class DefaultCommandRemoteSource(
    val serverConfigRepository: ServerConfigRepository,
    val client: HttpClient,
): CommandRemoteSource {
    override suspend fun getCommands(): Result<List<Command>> {
        val serverConfig = serverConfigRepository.getServerConfig()
            ?: return Result.failure(ServerNotConfiguredException())
        return safeCall<CommandsResponse> {
            client.get(Endpoints.CommandExecution.GetCommands.url(serverConfig.serverUrl)) {
                header("Authorization", "Bearer ${serverConfig.apiToken}")
            }
        }
            .map { responseData ->
                responseData.commands
            }
    }

    override suspend fun executeCommand(uuid: String): Result<Unit> {
        val serverConfig = serverConfigRepository.getServerConfig()
            ?: return Result.failure(ServerNotConfiguredException())
        return safeCall {
            client.post(Endpoints.CommandExecution.ExecuteCommand(uuid).url(serverConfig.serverUrl)) {
                header("Authorization", "Bearer ${serverConfig.apiToken}")
            }
        }
    }
}
