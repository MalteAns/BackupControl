package de.malteans.backup_control.db

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.datetime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
object BackupsTable : Table("backups") {
    val uuid = varchar("uuid", 255)
    val datetime = datetime("datetime")
    val fileName = varchar("file_name", 255)
    val success = bool("success").nullable()

    override val primaryKey = PrimaryKey(uuid, name = "PK_Backups_UUID")
}