package de.malteans.backup_control.backup.domain

interface BackupRemoteSource {
    suspend fun getBackups(): Result<List<Backup>>
    suspend fun getBackupLogFile(logFileName: String): Result<String>
}
