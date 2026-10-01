package de.malteans.backup_control.backup.services

import de.malteans.backup_control.ServerConstants
import de.malteans.backup_control.backup.db.BackupsTable
import de.malteans.backup_control.backup.db.FileDistributionTable
import de.malteans.backup_control.backup.domain.Backup
import de.malteans.backup_control.backup.domain.FileDistribution
import de.malteans.backup_control.backup.services.util.parseRsyncLog
import de.malteans.backup_control.backup.services.util.readLastNLines
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
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
                val totalFilesUuid = row[BackupsTable.totalFiles]
                val createdFilesUuid = row[BackupsTable.createdFiles]
                val deletedFilesUuid = row[BackupsTable.deletedFiles]

                val totalFiles = totalFilesUuid?.let { uuid ->
                    FileDistributionTable
                        .selectAll()
                        .where { FileDistributionTable.uuid eq uuid }
                        .singleOrNull()
                        ?.let { fdRow ->
                            FileDistribution(
                                regularFiles = fdRow[FileDistributionTable.regularFiles],
                                directories = fdRow[FileDistributionTable.directories],
                                fileLinks = fdRow[FileDistributionTable.fileLinks]
                            )
                        }
                }

                val createdFiles = createdFilesUuid?.let { uuid ->
                    FileDistributionTable
                        .selectAll()
                        .where { FileDistributionTable.uuid eq uuid }
                        .singleOrNull()
                        ?.let { fdRow ->
                            FileDistribution(
                                regularFiles = fdRow[FileDistributionTable.regularFiles],
                                directories = fdRow[FileDistributionTable.directories],
                                fileLinks = fdRow[FileDistributionTable.fileLinks]
                            )
                        }
                }

                val deletedFiles = deletedFilesUuid?.let { uuid ->
                    FileDistributionTable
                        .selectAll()
                        .where { FileDistributionTable.uuid eq uuid }
                        .singleOrNull()
                        ?.let { fdRow ->
                            FileDistribution(
                                regularFiles = fdRow[FileDistributionTable.regularFiles],
                                directories = fdRow[FileDistributionTable.directories],
                                fileLinks = fdRow[FileDistributionTable.fileLinks]
                            )
                        }
                }

                Backup(
                    uuid = row[BackupsTable.uuid],
                    startTime = row[BackupsTable.startTime]?.toKotlinLocalDateTime(),
                    fileName = row[BackupsTable.fileName],
                    success = row[BackupsTable.success],
                    duration = row[BackupsTable.duration],
                    totalFiles = totalFiles,
                    createdFiles = createdFiles,
                    deletedFiles = deletedFiles,
                    transferredRegularFiles = row[BackupsTable.transferredRegularFiles],
                    totalFileSize = row[BackupsTable.totalFileSize],
                    transferredFileSize = row[BackupsTable.transferredFileSize],
                    totalBytesSent = row[BackupsTable.totalBytesSent],
                    totalBytesReceived = row[BackupsTable.totalBytesReceived],
                    bytesPerSecond = row[BackupsTable.bytesPerSecond],
                    speedup = row[BackupsTable.speedup]
                )
            }
        }
    }

    override suspend fun updateBackups(): Result<Unit> = runCatching {
        val logFilesDir = System.getenv(ServerConstants.LOG_FILES_PATH_ENV_VAR)
            ?: throw IllegalStateException("${ServerConstants.LOG_FILES_PATH_ENV_VAR} environment variable not set")

        val logFilesDirFile = File(logFilesDir)
        val processedLogsDir = File(logFilesDirFile, "processedLogs")

        // If not existent create directory named "processedLogs"
        if (!processedLogsDir.exists()) processedLogsDir.mkdirs()

        // Iterate over files in logFilesDir (excluding files in processedLogs)
        logFilesDirFile.listFiles()?.filter { file ->
            file.isFile && file.parentFile == logFilesDirFile
        }?.forEach { file ->
            insertBackup(file.absolutePath)
                .onSuccess {
                    file.renameTo(File(processedLogsDir, file.name))
                }
                .onFailure { exception ->
                    throw exception
                }
        }
    }

    override suspend fun insertBackup(logFilePath: String): Result<Unit> = runCatching {
        val logText = readLastNLines(logFilePath, 20)

        val fileName = logFilePath.substringAfterLast('/')
        val backup = parseRsyncLog(fileName, logText)

        transaction(database) {
            fun insertFileDistribution(distribution: FileDistribution?): String? {
                if (distribution == null) return null
                val generatedUuid = Uuid.generateV7().toHexDashString()

                FileDistributionTable.insert {
                    it[FileDistributionTable.uuid] = generatedUuid
                    it[FileDistributionTable.regularFiles] = distribution.regularFiles
                    it[FileDistributionTable.directories] = distribution.directories
                    it[FileDistributionTable.fileLinks] = distribution.fileLinks
                }
                return generatedUuid
            }

            val totalFilesUuid = insertFileDistribution(backup.totalFiles)
            val createdFilesUuid = insertFileDistribution(backup.createdFiles)
            val deletedFilesUuid = insertFileDistribution(backup.deletedFiles)

            BackupsTable.insert { insert ->
                insert[BackupsTable.uuid] = Uuid.generateV7().toHexDashString()

                insert[BackupsTable.startTime] = backup.startTime?.let { LocalDateTime.parse(it.toString()) }

                insert[BackupsTable.fileName] = backup.fileName
                insert[BackupsTable.success] = backup.success
                insert[BackupsTable.duration] = backup.duration
                insert[BackupsTable.totalFiles] = totalFilesUuid
                insert[BackupsTable.createdFiles] = createdFilesUuid
                insert[BackupsTable.deletedFiles] = deletedFilesUuid
                insert[BackupsTable.transferredRegularFiles] = backup.transferredRegularFiles
                insert[BackupsTable.totalFileSize] = backup.totalFileSize
                insert[BackupsTable.transferredFileSize] = backup.transferredFileSize
                insert[BackupsTable.totalBytesSent] = backup.totalBytesSent
                insert[BackupsTable.totalBytesReceived] = backup.totalBytesReceived
                insert[BackupsTable.bytesPerSecond] = backup.bytesPerSecond
                insert[BackupsTable.speedup] = backup.speedup
            }
        }
    }

    override suspend fun removeBackup(uuid: String): Result<Unit> = runCatching {
        transaction(database) {
            BackupsTable.deleteWhere { BackupsTable.uuid eq uuid }
        }
    }
}
