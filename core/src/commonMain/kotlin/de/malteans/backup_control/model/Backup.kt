package de.malteans.backup_control.model

import kotlinx.datetime.LocalDateTime

data class Backup(
    val uuid: String,
    val startTime: LocalDateTime,
    val fileName: String,
    val success: Boolean?,
    val duration: Int?,
    val totalFiles: FileDistribution?,
    val createdFiles: FileDistribution?,
    val deletedFiles: FileDistribution?,
    val transferredRegularFiles: Int?,
    val totalFileSize: Long?,
    val transferredFileSize: Long?,
    val totalBytesSent: Long?,
    val totalBytesReceived: Long?,
    val bytesPerSecond: Int?,
    val speedup: Int?,
)
