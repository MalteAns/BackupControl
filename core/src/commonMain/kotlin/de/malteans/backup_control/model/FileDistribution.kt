package de.malteans.backup_control.model

import kotlinx.serialization.Serializable

@Serializable
data class FileDistribution(
    val regularFiles: Int,
    val directories: Int,
    val fileLinks: Int,
)
