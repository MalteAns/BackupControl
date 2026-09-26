package de.malteans.backup_control.backups.domain

import kotlinx.datetime.LocalDateTime

data class Backup(
    val uuid: String,
    val datetime: LocalDateTime,
    val fileName: String,
    val success: Boolean?,
)
