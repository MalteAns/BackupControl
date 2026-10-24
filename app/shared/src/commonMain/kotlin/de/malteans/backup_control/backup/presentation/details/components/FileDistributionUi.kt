package de.malteans.backup_control.backup.presentation.details.components

import de.malteans.backup_control.backup.domain.FileDistribution
import de.malteans.backup_control.backup.presentation.components.pow
import de.malteans.backup_control.backup.presentation.components.round

data class FileDistributionUi(
    val regularFiles: String = "—",
    val directories: String = "—",
    val fileLinks: String = "—",
)

fun FileDistribution.toUiModel() = FileDistributionUi(
    regularFiles = this.regularFiles.formatFileAmount(),
    directories = this.directories.formatFileAmount(),
    fileLinks = this.fileLinks.formatFileAmount(),
)

private fun Int.formatFileAmount(): String {
    return when {
        this < 10L.pow(3) -> this.toString()
        this < 10L.pow(6) -> "${this.toLong().round(3)} K"
        else -> "${this.toLong().round(6)} M"
    }
}
