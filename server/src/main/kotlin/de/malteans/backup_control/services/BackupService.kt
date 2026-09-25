package de.malteans.backup_control.services

import de.malteans.backup_control.ServerConstants
import de.malteans.backup_control.db.BackupsTable
import de.malteans.backup_control.model.Backup
import de.malteans.backup_control.services.util.readLastNLines
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface BackupService {
    suspend fun getBackups(): Result<List<Backup>>
    suspend fun updateBackups(): Result<Unit>

    suspend fun insertBackup(logFilePath: String): Result<Unit>
    suspend fun removeBackup(uuid: String): Result<Unit>
}

@OptIn(ExperimentalUuidApi::class)
class BackupServiceImpl(
    private val database: Database,
) : BackupService {
    
    private companion object {
        val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm")!!
    }

    override suspend fun getBackups(): Result<List<Backup>> = runCatching {
        transaction(database) {
            BackupsTable.selectAll().map { row ->
                Backup(
                    uuid = row[BackupsTable.uuid],
                    datetime = row[BackupsTable.datetime].toString(),
                    fileName = row[BackupsTable.fileName],
                    success = row[BackupsTable.success]
                )
            }
        }
    }

    override suspend fun updateBackups(): Result<Unit> = runCatching {
        val logFilesDir = System.getenv(ServerConstants.LOG_FILES_PATH_ENV_VAR)
            ?: throw IllegalStateException("${ServerConstants.LOG_FILES_PATH_ENV_VAR} environment variable not set")

        val cutoffTime = transaction(database) {
            BackupsTable
                .select(BackupsTable.datetime)
                .orderBy(BackupsTable.datetime, SortOrder.DESC)
                .limit(1)
                .map { it[BackupsTable.datetime] }
                .firstOrNull()
        } ?: LocalDateTime.MIN

        val newLogFiles = File(logFilesDir)
            .listFiles { file -> // Filter:
                file.isFile && file.name.endsWith("_log-backup.txt") && try {
                    val dateTimeStr = file.name.substringBeforeLast("_")
                    val fileTime = LocalDateTime.parse(dateTimeStr, DATE_FORMATTER)
                    fileTime > cutoffTime
                } catch (e: Exception) {
                    false
                }
            }?.toList()

        newLogFiles?.forEach { file ->
            insertBackup(file.absolutePath).getOrThrow()
        }
    }

    override suspend fun insertBackup(logFilePath: String): Result<Unit> = runCatching {
        val fileName = logFilePath.substringAfterLast(File.separator)

        // Parse timestamp from filename (e.g. 2026-08-04_01-04_log-backup.txt -> 2026-08-04T01:04:00)
        val dateTimeStr = fileName.substringBeforeLast("_")
        val timestamp = LocalDateTime.parse(dateTimeStr, DATE_FORMATTER)

        // Read second last line to determine success
        val success = readLastNLines(logFilePath, 2).let { lines ->
            if (lines.size >= 2) {
                lines[0]!!.contains("error", ignoreCase = true).not()
            } else {
                null // Not enough lines to determine
            }
        }

        transaction(database) {
            BackupsTable.insert { row ->
                row[BackupsTable.uuid] = Uuid.generateV7().toHexDashString()
                row[BackupsTable.fileName] = fileName
                row[BackupsTable.datetime] = timestamp
                row[BackupsTable.success] = success
            }
        }
    }

    override suspend fun removeBackup(uuid: String): Result<Unit> = runCatching {
        transaction(database) {
            BackupsTable.deleteWhere { BackupsTable.uuid eq uuid }
        }
    }
}
