package de.malteans.backup_control.dto

import kotlinx.serialization.Serializable

@Serializable
data class BackupsResponse(
    val backups: List<BackupDto>
)
