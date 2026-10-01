package de.malteans.backup_control.backup.db

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.datetime

object BackupsTable : Table("backups") {
    val uuid = varchar("uuid", 255)
    val startTime = datetime("start_time").nullable()
    val fileName = varchar("file_name", 255)

    val success = bool("success").nullable()
    val duration = integer("duration").nullable()
    val totalFiles = reference(
        "total_files",
        FileDistributionTable.uuid,
        onDelete = ReferenceOption.SET_NULL
    ).nullable()
    val createdFiles = reference(
        "created_files",
        FileDistributionTable.uuid,
        onDelete = ReferenceOption.SET_NULL
    ).nullable()
    val deletedFiles = reference(
        "deleted_files",
        FileDistributionTable.uuid,
        onDelete = ReferenceOption.SET_NULL
    ).nullable()
    val transferredRegularFiles = integer("transferred_regular_files").nullable()
    val totalFileSize = long("total_file_size").nullable()
    val transferredFileSize = long("transferred_file_size").nullable()
    val totalBytesSent = long("total_bytes_sent").nullable()
    val totalBytesReceived = long("total_bytes_received").nullable()
    val bytesPerSecond = integer("bytes_per_second").nullable()
    val speedup = integer("speedup").nullable()

    override val primaryKey = PrimaryKey(uuid, name = "PK_Backups_UUID")
}