package de.malteans.backup_control.backups.data

import de.malteans.backup_control.backups.domain.Backup
import de.malteans.backup_control.model.BackupDto
import kotlinx.datetime.LocalDateTime

fun BackupDto.toDomain() = Backup(
    uuid = uuid,
    datetime = LocalDateTime.parse(datetime),
    fileName = fileName,
    success = success
)
