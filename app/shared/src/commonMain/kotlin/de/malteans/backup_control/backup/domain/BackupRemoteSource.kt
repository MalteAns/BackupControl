package de.malteans.backup_control.backup.domain

import de.malteans.backup_control.model.Backup

interface BackupRemoteSource {
    suspend fun getBackups(): Result<List<Backup>>
    suspend fun getBackupLogFile(logFileName: String): Result<String>
}
