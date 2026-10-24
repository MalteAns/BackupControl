package de.malteans.backup_control.backup.data

import de.malteans.backup_control.backup.domain.Backup
import kotlinx.datetime.LocalDateTime

fun BackupDto.toDomain() = Backup(
    uuid = uuid,
    startTime = startTime?.let { LocalDateTime.parse(it) },
    fileName = fileName,
    success = success,
    duration = duration,
    totalFiles = totalFiles,
    createdFiles = createdFiles,
    deletedFiles = deletedFiles,
    transferredRegularFiles = transferredRegularFiles,
    totalFileSize = totalFileSize,
    transferredFileSize = transferredFileSize,
    totalBytesSent = totalBytesSent,
    totalBytesReceived = totalBytesReceived,
    bytesPerSecond = bytesPerSecond,
    speedup = speedup,
)

fun Backup.toDto() = BackupDto(
    uuid = uuid,
    startTime = startTime?.toString(),
    fileName = fileName,
    success = success,
    duration = duration,
    totalFiles = totalFiles,
    createdFiles = createdFiles,
    deletedFiles = deletedFiles,
    transferredRegularFiles = transferredRegularFiles,
    totalFileSize = totalFileSize,
    transferredFileSize = transferredFileSize,
    totalBytesSent = totalBytesSent,
    totalBytesReceived = totalBytesReceived,
    bytesPerSecond = bytesPerSecond,
    speedup = speedup,
)
