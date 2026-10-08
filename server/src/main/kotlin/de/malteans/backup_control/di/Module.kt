package de.malteans.backup_control.di

import de.malteans.backup_control.ServerConstants
import de.malteans.backup_control.backup.db.BackupsTable
import de.malteans.backup_control.backup.db.FileDistributionTable
import de.malteans.backup_control.backup.services.BackupService
import de.malteans.backup_control.backup.services.BackupServiceImpl
import de.malteans.backup_control.commandExecution.db.CommandTable
import de.malteans.backup_control.commandExecution.service.CommandService
import de.malteans.backup_control.commandExecution.service.CommandServiceImpl
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import org.koin.dsl.module

val module = module {
    single<Database> {
        val dbPath = System.getenv(ServerConstants.DATABASE_PATH_ENV_VAR)
            ?: ServerConstants.DEFAULT_DATABASE_PATH

        val database = Database.connect(
            url = "jdbc:sqlite:$dbPath",
            driver = "org.sqlite.JDBC",
        )

        transaction(database) {
            MigrationUtils.statementsRequiredForDatabaseMigration(
                BackupsTable, FileDistributionTable,
                CommandTable,
            withLogs = true).forEach { statement ->
                exec(statement)
            }
        }

        database
    }
    
    single<BackupService> { BackupServiceImpl(get()) }
    single<CommandService> { CommandServiceImpl(get()) }
}
