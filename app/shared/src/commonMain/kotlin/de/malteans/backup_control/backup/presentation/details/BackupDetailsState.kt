package de.malteans.backup_control.backup.presentation.details

import de.malteans.backup_control.backup.domain.Backup
import de.malteans.backup_control.backup.presentation.details.components.FileDistributionUi
import de.malteans.backup_control.core.presentation.util.UiText

data class BackupDetailsState(
    val isLoading: Boolean = true,
    val error: String? = null,

    val selectedBackup: Backup? = null,
    val startTime: String = "—",
    val fileName: String = "—",
    val success: Boolean? = null,
    val duration: UiText = UiText.DynamicString("—"),
    val totalFileSize: String = "—",

    val totalFiles: FileDistributionUi = FileDistributionUi(),
    val createdFiles: FileDistributionUi = FileDistributionUi(),
    val deletedFiles: FileDistributionUi = FileDistributionUi(),

    val transferredRegularFiles: String = "—",
    val transferredFileSize: String = "—",
    val totalBytesSent: String = "—",
    val totalBytesReceived: String = "—",
    val bytesPerSecond: String = "—",
    val speedup: String = "—",

    val logFileContent: String? = null,
)
