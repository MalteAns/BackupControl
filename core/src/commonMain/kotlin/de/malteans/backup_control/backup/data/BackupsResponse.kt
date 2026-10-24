package de.malteans.backup_control.backup.data

import kotlinx.serialization.Serializable

@Serializable
data class BackupsResponse(
    val backups: List<BackupDto>
)
