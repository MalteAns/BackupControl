package de.malteans.backup_control.backup.db

import org.jetbrains.exposed.v1.core.Table

object FileDistributionTable: Table("file_distribution") {
    val uuid = varchar("uuid", 255)
    val regularFiles = integer("regular_files")
    val directories = integer("directories")
    val fileLinks = integer("file_links")

    override val primaryKey = PrimaryKey(uuid, name = "PK_file_distribution_uuid")
}