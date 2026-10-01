package de.malteans.backup_control.backup.presentation.details

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.backup_control.backup.presentation.components.formatByteSize
import de.malteans.backup_control.backup.presentation.components.formatDuration
import de.malteans.backup_control.backup.presentation.details.components.FileDistributionTags
import de.malteans.backup_control.backup.presentation.details.components.FileDistributionUi
import de.malteans.backup_control.backup.presentation.details.components.toUiModel
import de.malteans.backup_control.core.presentation.theme.AppTheme
import de.malteans.backup_control.core.presentation.util.CircularLoadingScreen
import de.malteans.backup_control.core.presentation.util.ErrorScreen
import de.malteans.backup_control.core.presentation.util.UiText
import de.malteans.backup_control.model.Backup
import de.malteans.backup_control.model.FileDistribution
import kotlinx.datetime.LocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun BackupDetailsScreenRoot(
    logFileName: String,
    viewModel: BackupDetailsViewModel = koinViewModel {
        parametersOf(logFileName)
    }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BackupDetailsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
fun BackupDetailsScreen(
    state: BackupDetailsState,
    onAction: (BackupDetailsAction) -> Unit,
) {
    if (state.isLoading || state.selectedBackup == null) {
        CircularLoadingScreen()
        return
    }
    if (state.error != null) {
        ErrorScreen(state.error)
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        GeneralInfoCard(
            startTime = state.startTime,
            success = state.success,
            duration = state.duration.asString(),
            totalFileSize = state.totalFileSize,
        )
        FileDistributionCard(
            state.totalFiles,
            state.createdFiles,
            state.deletedFiles,
        )
        DetailedInformationCard(
            transferredRegularFiles = state.transferredRegularFiles,
            transferredFileSize = state.transferredFileSize,
            totalBytesSent = state.totalBytesSent,
            totalBytesReceived = state.totalBytesReceived,
            bytesPerSecond = state.bytesPerSecond,
            speedup = state.speedup
        )
    }
}

@Composable
@OptIn(ExperimentalGridApi::class)
private fun GeneralInfoCard(
    startTime: String,
    success: Boolean?,
    duration: String,
    totalFileSize: String,
) = BackupDetailsCard {
    Text("Start:", maxLines = 1)
    Text(startTime, maxLines = 1)
    Text("Success:", maxLines = 1)
    Text(if (success == true) "Yes" else if (success == false) "No" else "Unknown", maxLines = 1)
    Text("Duration:", maxLines = 1)
    Text(duration, maxLines = 1)
}

@Composable
@OptIn(ExperimentalGridApi::class)
private fun FileDistributionCard(
    totalFiles: FileDistributionUi,
    createdFiles: FileDistributionUi,
    deletedFiles: FileDistributionUi,
) = BackupDetailsCard{
    Text(
        "Total",
        modifier = Modifier.gridItem(alignment = Alignment.CenterStart)
    )
    FileDistributionTags(
        distribution = totalFiles,
    )
    Text(
        "Created",
        modifier = Modifier.gridItem(alignment = Alignment.CenterStart)
    )
    FileDistributionTags(
        distribution = createdFiles,
    )
    Text(
        "Deleted",
        modifier = Modifier.gridItem(alignment = Alignment.CenterStart)
    )
    FileDistributionTags(
        distribution = deletedFiles,
    )
}

@Composable
@OptIn(ExperimentalGridApi::class)
private fun DetailedInformationCard(
    transferredRegularFiles: String,
    transferredFileSize: String,
    totalBytesSent: String,
    totalBytesReceived: String,
    bytesPerSecond: String,
    speedup: String,
) = BackupDetailsCard(
    gridConfig = {
        column(1.fr)
        repeat(6) { row(GridTrackSize.MinContent) }
        rowGap(4.dp)
    }
) {
    Text("Transferred Regular Files: $transferredRegularFiles")
    Text("Transferred File Size: $transferredFileSize")
    Text("Total Bytes Sent: $totalBytesSent")
    Text("Total Bytes Received: $totalBytesReceived")
    Text("Bytes Per Second: $bytesPerSecond")
    Text("Speedup: $speedup")
}




@Composable
@OptIn(ExperimentalGridApi::class)
private fun BackupDetailsCard(
    gridConfig: GridConfigurationScope.() -> Unit = {
        column(GridTrackSize.MaxContent)
        column(GridTrackSize.Auto)
        columnGap(8.dp)
    },
    gridModifier: Modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
    content: @Composable GridScope.() -> Unit,
) {
    ElevatedCard(
        modifier = modifier
    ) {
        Grid(
            config = gridConfig,
            modifier = gridModifier,
            content = content
        )
    }
}

@Preview
@Composable
fun BackupDetailsScreenPreview() {
    AppTheme(
        darkTheme = true,
    ) {
        val backup = Backup(
            startTime = LocalDateTime(2026, 9, 29, 13, 12),
            fileName = "Backup.log",
            success = true,
            duration = 350,
            totalFiles = FileDistribution(
                regularFiles = 2310,
                directories = 220020290,
                fileLinks = 4,
            ),
            createdFiles = FileDistribution(
                regularFiles = 30000000,
                directories = 20000000,
                fileLinks = 2,
            ),
            deletedFiles = FileDistribution(
                regularFiles = 2,
                directories = 1,
                fileLinks = 0,
            ),
            transferredRegularFiles = 34,
            totalFileSize = 2032103L,
            transferredFileSize = 2012L,
            totalBytesSent = 1203L,
            totalBytesReceived = 300L,
            bytesPerSecond = 3,
            speedup = 54,
        )

        Surface {
            BackupDetailsScreen(
                state = BackupDetailsState(
                    isLoading = false,
                    error = null,
                    selectedBackup = backup,

                    startTime = backup.startTime?.toString()?.replace("T", " ") ?: "—",
                    fileName = backup.fileName,
                    success = backup.success,
                    duration = backup.duration?.formatDuration() ?: UiText.DynamicString("—"),
                    totalFileSize = backup.totalFileSize?.formatByteSize() ?: "—",

                    totalFiles = backup.totalFiles?.toUiModel() ?: FileDistributionUi(),
                    createdFiles = backup.createdFiles?.toUiModel() ?: FileDistributionUi(),
                    deletedFiles = backup.deletedFiles?.toUiModel() ?: FileDistributionUi(),

                    transferredRegularFiles = backup.transferredRegularFiles?.toString() ?: "—",
                    transferredFileSize = backup.transferredFileSize?.formatByteSize() ?: "—",
                    totalBytesSent = backup.totalBytesSent?.formatByteSize() ?: "—",
                    totalBytesReceived = backup.totalBytesReceived?.formatByteSize() ?: "—",
                    bytesPerSecond = backup.bytesPerSecond?.let { "${it.toLong().formatByteSize()}/s" } ?: "—",
                    speedup = backup.speedup?.toString() ?: "—",

                    logFileContent = "Toller Content"
                ),
                onAction = { action ->

                }
            )
        }
    }
}
