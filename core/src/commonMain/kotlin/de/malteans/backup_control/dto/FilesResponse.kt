package de.malteans.backup_control.dto

import kotlinx.serialization.Serializable

@Serializable
data class FilesResponse(
    val files: List<String>
)
