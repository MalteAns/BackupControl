package de.malteans.backup_control.services

import de.malteans.backup_control.ServerConstants
import de.malteans.backup_control.db.BackupsTable
import de.malteans.backup_control.db.FileDistributionTable
import de.malteans.backup_control.model.Backup
import de.malteans.backup_control.model.FileDistribution
import de.malteans.backup_control.services.util.readLastNLines
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
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

        val cutoffTime = transaction(database) {
            BackupsTable
                .select(BackupsTable.startTime)
                .orderBy(BackupsTable.startTime, SortOrder.DESC)
                .limit(1)
                .map { it[BackupsTable.startTime] }
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
        val logText = readLastNLines(logFilePath, 17)
        val firstLine = File(logFilePath).useLines { it.firstOrNull() }
        if (firstLine != null) {
            logText.add(0, firstLine)
        }

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

    /**
     * @param logLines consists of the first line with the timestamp of the start and then the last 17 lines which contain the statistics after a successful backup
     */
    fun parseRsyncLog(fileName: String, logLines: MutableList<String>): Backup {
        val isFailed = logLines.any { it.contains("Backup failed") }
        val validLines = logLines.filter { it.isNotBlank() }

        // Helper to extract dates from either "2026/09/27 01:02:09" OR "Sun 27 Sep 01:02:09 CEST 2026"
        fun extractDate(line: String?): LocalDateTime? {
            if (line == null) return null
            val match = """([A-Za-z]{3}\s+\d{1,2}\s+[A-Za-z]{3}\s+\d{2}:\d{2}:\d{2}\s+[A-Za-z]{3,4}\s+\d{4})""".toRegex().find(line)
            if (match != null) {
                val fmt = DateTimeFormatter.ofPattern("EEE d MMM HH:mm:ss z yyyy", Locale.ENGLISH)
                try { return ZonedDateTime.parse(match.value, fmt).toLocalDateTime() } catch (e: Exception) {}
            }
            return null
        }

        // 1. Parse Datetime (Strictly from the first line)
        val parsedStartTime = extractDate(validLines.firstOrNull())

        // 2. Parse Duration (Difference between first and last line)
        val endDatetime = extractDate(validLines.lastOrNull())

        val computedDuration = if (parsedStartTime != null && endDatetime != null) {
            val zoneOffset = ZoneId.systemDefault().rules.getOffset(endDatetime)
            (endDatetime.toEpochSecond(zoneOffset) - parsedStartTime.toEpochSecond(zoneOffset)).toInt()
        } else null

        // 3. Early return if backup failed
        if (isFailed) {
            return Backup(
                fileName = fileName,
                startTime = parsedStartTime?.toKotlinLocalDateTime(),
                success = false,
                duration = computedDuration
            )
        }

        // 4. Full parse if backup succeeded
        fun String.toCleanLong(): Long = this.replace(",", "").toLongOrNull() ?: 0L
        fun String.toCleanInt(): Int = this.replace(",", "").substringBefore('.').toIntOrNull() ?: 0

        var totalFiles: FileDistribution? = null
        var createdFiles: FileDistribution? = null
        var deletedFiles: FileDistribution? = null
        var transRegFiles: Int? = null
        var totalSize: Long? = null
        var transSize: Long? = null
        var bytesSent: Long? = null
        var bytesReceived: Long? = null
        var bytesPerSec: Int? = null
        var parsedSpeedup: Int? = null

        val distributionRegex = """\(reg:\s*([\d,]+),\s*dir:\s*([\d,]+),\s*link:\s*([\d,]+)\)""".toRegex()

        fun parseFileDistribution(line: String): FileDistribution? {
            val match = distributionRegex.find(line)
            if (match != null) {
                return FileDistribution(match.groupValues[1].toCleanInt(), match.groupValues[2].toCleanInt(), match.groupValues[3].toCleanInt())
            }
            val singleNumberMatch = """:\s*([\d,]+)$""".toRegex().find(line)
            val count = singleNumberMatch?.groupValues?.get(1)?.toCleanInt() ?: 0
            return if (count == 0) FileDistribution(0, 0, 0) else null
        }

        validLines.forEach { line ->
            when {
                line.contains("Number of files:") -> totalFiles = parseFileDistribution(line)
                line.contains("Number of created files:") -> createdFiles = parseFileDistribution(line)
                line.contains("Number of deleted files:") -> deletedFiles = parseFileDistribution(line)
                line.contains("Number of regular files transferred:") ->
                    transRegFiles = """transferred:\s*([\d,]+)""".toRegex().find(line)?.groupValues?.get(1)?.toCleanInt()
                line.contains("Total file size:") ->
                    totalSize = """size:\s*([\d,]+)""".toRegex().find(line)?.groupValues?.get(1)?.toCleanLong()
                line.contains("Total transferred file size:") ->
                    transSize = """size:\s*([\d,]+)""".toRegex().find(line)?.groupValues?.get(1)?.toCleanLong()
                line.contains("bytes/sec") -> {
                    val match = """sent\s+([\d,]+)\s+bytes\s+received\s+([\d,]+)\s+bytes\s+([\d.,]+)\s+bytes/sec""".toRegex().find(line)
                    match?.let {
                        bytesSent = it.groupValues[1].toCleanLong()
                        bytesReceived = it.groupValues[2].toCleanLong()
                        bytesPerSec = it.groupValues[3].toCleanInt()
                    }
                }
                line.contains("speedup is") ->
                    parsedSpeedup = """speedup is\s+([\d.,]+)""".toRegex().find(line)?.groupValues?.get(1)?.toCleanInt()
            }
        }

        return Backup(
            fileName = fileName,
            startTime = parsedStartTime?.toKotlinLocalDateTime(),
            success = true,
            duration = computedDuration,
            totalFiles = totalFiles,
            createdFiles = createdFiles,
            deletedFiles = deletedFiles,
            transferredRegularFiles = transRegFiles,
            totalFileSize = totalSize,
            transferredFileSize = transSize,
            totalBytesSent = bytesSent,
            totalBytesReceived = bytesReceived,
            bytesPerSecond = bytesPerSec,
            speedup = parsedSpeedup
        )
    }
}
