package de.malteans.backup_control.backup.data

import de.malteans.backup_control.backup.domain.FileDistribution
import kotlinx.serialization.Serializable

@Serializable
data class BackupDto(
    val uuid: String,
    val startTime: String?,
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
