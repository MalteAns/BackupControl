package de.malteans.backup_control.backups.data

import de.malteans.backup_control.Endpoints
import de.malteans.backup_control.backups.domain.Backup
import de.malteans.backup_control.backups.domain.BackupRepository
import de.malteans.backup_control.core.data.AnsLog
import de.malteans.backup_control.core.data.network.ApiConfig
import de.malteans.backup_control.core.data.network.safeCall
import de.malteans.backup_control.dto.BackupsResponse
import io.ktor.client.*
import io.ktor.client.request.*

class BackupRepositoryImpl(
    private val apiConfig: ApiConfig,
    private val client: HttpClient
) : BackupRepository {
    companion object {
        const val TAG = "BackupRepositoryImpl"
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
