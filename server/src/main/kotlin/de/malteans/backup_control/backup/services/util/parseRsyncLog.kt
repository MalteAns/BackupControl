package de.malteans.backup_control.backup.services.util

import de.malteans.backup_control.model.Backup
import de.malteans.backup_control.model.FileDistribution
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * @param logLines consists of the first line with the timestamp of the start and then the last 17 lines which contain the statistics after a successful backup
 */
fun parseRsyncLog(fileName: String, logLines: MutableList<String>): Backup {
    val isFailed = logLines.any { it.contains("Backup failed") }
    val validLines = logLines.filter { it.isNotBlank() }

    // Helper to extract dates from either "2026/09/27 01:02:09" OR "Sun 27 Sep 01:02:09 CEST 2026"
    fun extractDate(line: String?): LocalDateTime? {
        if (line == null) return null
        val formatter = DateTimeFormatter.ofPattern("EEE d MMM HH:mm:ss zzz yyyy")
        return runCatching {
            ZonedDateTime.parse(line, formatter).toLocalDateTime()
        }.getOrNull()
    }

    // 1. Parse Datetime (Strictly from the first line)
    val parsedStartTime = extractDate(validLines[validLines.size - 2])

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

    fun parseFileDistribution(line: String): FileDistribution {
        if (line.contains("files: 0")) return FileDistribution(0, 0, 0)

        val parts = line.substringAfter("(").substringBefore(")").split(", ")
        var regCount = 0
        var dirCount = 0
        var linkCount = 0
        parts.forEach { part ->
            when {
                part.startsWith("reg:") -> {
                    regCount = part.substringAfter("reg: ").toCleanInt()
                }
                part.startsWith("dir:") -> {
                    dirCount = part.substringAfter("dir: ").toCleanInt()
                }
                part.startsWith("link:") -> {
                    linkCount = part.substringAfter("link: ").toCleanInt()
                }
            }
        }
        return FileDistribution(regCount, dirCount, linkCount)
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