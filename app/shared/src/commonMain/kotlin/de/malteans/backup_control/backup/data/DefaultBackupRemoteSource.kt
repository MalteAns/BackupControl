package de.malteans.backup_control.backup.data

import de.malteans.backup_control.Endpoints
import de.malteans.backup_control.backup.domain.Backup
import de.malteans.backup_control.backup.domain.BackupRemoteSource
import de.malteans.backup_control.core.data.AnsLog
import de.malteans.backup_control.core.data.network.ApiConfig
import de.malteans.backup_control.core.data.network.safeCall
import io.ktor.client.*
import io.ktor.client.request.*

class DefaultBackupRemoteSource(
    private val apiConfig: ApiConfig,
    private val client: HttpClient
) : BackupRemoteSource {
    companion object {
        const val TAG = "DefaultBackupRemoteSource"
    }

    override suspend fun getBackups(): Result<List<Backup>> {
        return safeCall<BackupsResponse> {
            client.get(Endpoints.Backups.GetAll.url) {
                header("Authorization", "Bearer ${apiConfig.apiToken}")
            }
        }
            .map { response ->
                AnsLog.d(TAG, "Mapping result to list of Backup objects")
                response.backups.map { it.toDomain() }
            }
    }

    override suspend fun getBackupLogFile(logFileName: String): Result<String> {
        return safeCall<String> {
            client.get(Endpoints.Files.GetFile(logFileName).url) {
                header("Authorization", "Bearer ${apiConfig.apiToken}")
            }
        }
    }
}
