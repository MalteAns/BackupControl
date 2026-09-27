package de.malteans.backup_control.backups.domain

interface BackupRepository {
    suspend fun getBackups(): Result<List<Backup>>
    suspend fun getBackupLogFile(logFileName: String): Result<String>
}
