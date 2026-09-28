package de.malteans.backup_control.mappers

import de.malteans.backup_control.dto.BackupDto
import de.malteans.backup_control.model.Backup
import kotlinx.datetime.LocalDateTime

fun BackupDto.toDomain() = Backup(
    uuid = uuid,
    datetime = LocalDateTime.parse(datetime),
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
    datetime = datetime.toString(),
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
