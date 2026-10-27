package de.malteans.backup_control.backup.data

import de.malteans.backup_control.Endpoints
import de.malteans.backup_control.backup.domain.Backup
import de.malteans.backup_control.backup.domain.BackupRemoteSource
import de.malteans.backup_control.core.data.AnsLog
import de.malteans.backup_control.core.data.network.safeCall
import de.malteans.backup_control.core.data.settings.ServerConfigRepository
import de.malteans.backup_control.core.data.settings.ServerNotConfiguredException
import io.ktor.client.*
import io.ktor.client.request.*

class DefaultBackupRemoteSource(
    private val serverConfigRepository: ServerConfigRepository,
    private val client: HttpClient
) : BackupRemoteSource {
    companion object {
        const val TAG = "DefaultBackupRemoteSource"
    }

    override suspend fun getBackups(): Result<List<Backup>> {
        val serverConfig = serverConfigRepository.getServerConfig()
            ?: return Result.failure(ServerNotConfiguredException())
        return safeCall<BackupsResponse> {
            client.get(Endpoints.Backup.GetAll.url(serverConfig.serverUrl)) {
                header("Authorization", "Bearer ${serverConfig.apiToken}")
            }
        }
            .map { response ->
                AnsLog.d(TAG, "Mapping result to list of Backup objects")
                response.backups.map { it.toDomain() }
            }
    }

    override suspend fun getBackupLogFile(logFileName: String): Result<String> {
        val serverConfig = serverConfigRepository.getServerConfig()
            ?: return Result.failure(ServerNotConfiguredException())
        return safeCall<String> {
            client.get(Endpoints.Files.GetFile(logFileName).url(serverConfig.serverUrl)) {
                header("Authorization", "Bearer ${serverConfig.apiToken}")
            }
        }
    }
}
