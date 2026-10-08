package de.malteans.backup_control.commandExecution.db

import org.jetbrains.exposed.v1.core.Table

object CommandTable: Table("commands") {
    val uuid = varchar("uuid", 255)
    val name = varchar("name", 255).nullable()
    val command = varchar("command", 255)

    override val primaryKey = PrimaryKey(uuid)
}