package de.malteans.backup_control.commandExecution.data

import de.malteans.backup_control.Endpoints
import de.malteans.backup_control.commandExecution.domain.Command
import de.malteans.backup_control.commandExecution.domain.CommandRemoteSource
import de.malteans.backup_control.core.data.network.ApiConfig
import de.malteans.backup_control.core.data.network.safeCall
import io.ktor.client.*
import io.ktor.client.request.*

class DefaultCommandRemoteSource(
    val apiConfig: ApiConfig,
    val client: HttpClient,
): CommandRemoteSource {
    override suspend fun getCommands(): Result<List<Command>> {
        return safeCall<CommandsResponse> {
            client.get(Endpoints.CommandExecution.GetCommands.url) {
                header("Authorization", "Bearer ${apiConfig.apiToken}")
            }
        }
            .map { responseData ->
                responseData.commands
            }
    }

    override suspend fun executeCommand(uuid: String): Result<Unit> {
        return safeCall {
            client.post(Endpoints.CommandExecution.ExecuteCommand(uuid).url) {
                header("Authorization", "Bearer ${apiConfig.apiToken}")
            }
        }
    }
}