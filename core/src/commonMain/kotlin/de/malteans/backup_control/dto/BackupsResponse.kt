package de.malteans.backup_control.dto

import de.malteans.backup_control.model.BackupDto
import kotlinx.serialization.Serializable

@Serializable
data class BackupsResponse(
    val backups: List<BackupDto>
)
