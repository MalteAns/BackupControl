package de.malteans.backup_control.model

import kotlinx.serialization.Serializable

@Serializable
data class Backup(
    val uuid: String,
    val datetime: String,
    val fileName: String,
    val success: Boolean?,
)
