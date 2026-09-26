package de.malteans.backup_control.backups.domain

interface BackupRepository {
    suspend fun getBackups(): Result<List<Backup>>
}
