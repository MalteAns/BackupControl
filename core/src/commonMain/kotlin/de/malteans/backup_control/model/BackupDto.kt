package de.malteans.backup_control.model

import kotlinx.serialization.Serializable

@Serializable
data class BackupDto(
    val uuid: String,
    val datetime: String,
    val fileName: String,
    val success: Boolean?,
)
