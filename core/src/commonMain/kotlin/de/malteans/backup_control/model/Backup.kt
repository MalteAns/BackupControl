package de.malteans.backup_control.model

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Backup(
    val uuid: String = Uuid.generateV7().toHexDashString(),
    val startTime: LocalDateTime? = null,
    val fileName: String,
    val success: Boolean? = null,
    /** Duration in seconds */
    val duration: Int? = null,
    val totalFiles: FileDistribution? = null,
    val createdFiles: FileDistribution? = null,
    val deletedFiles: FileDistribution? = null,
    val transferredRegularFiles: Int? = null,
    val totalFileSize: Long? = null,
    val transferredFileSize: Long? = null,
    val totalBytesSent: Long? = null,
    val totalBytesReceived: Long? = null,
    val bytesPerSecond: Int? = null,
    val speedup: Int? = null,
)
