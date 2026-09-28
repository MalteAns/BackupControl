package de.malteans.backup_control.backup.presentation.details.compose

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import backupcontrol.app.shared.generated.resources.Res
import backupcontrol.app.shared.generated.resources.directories
import backupcontrol.app.shared.generated.resources.file_links
import backupcontrol.app.shared.generated.resources.regular_files
import de.malteans.backup_control.model.FileDistribution
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun FileDistributionTags(
    distribution: FileDistribution,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    FlowRow(modifier = modifier) {
        (0..2).forEach { index ->
            val toolTipState = rememberTooltipState(isPersistent = false)
            TooltipBox(
                state = toolTipState,
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                    positioning = TooltipAnchorPosition.Above
                ),
                tooltip = {
                    Text(
                        text = when (index) {
                            0 -> stringResource(Res.string.regular_files)
                            1 -> stringResource(Res.string.directories)
                            else -> stringResource(Res.string.file_links)
                        }
                    )
                },
            ) {
                ElevatedAssistChip(
                    onClick = { scope.launch { toolTipState.show() } },
                    label = {
                        Text(
                            text = when (index) {
                                0 -> distribution.regularFiles
                                1 -> distribution.directories
                                else -> distribution.fileLinks
                            }.toString()
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = when (index) {
                                0 -> Icons.Default.FileCopy
                                1 -> Icons.Default.Folder
                                else -> Icons.Default.Link
                            },
                            contentDescription = when (index) {
                                0 -> stringResource(Res.string.regular_files)
                                1 -> stringResource(Res.string.directories)
                                else -> stringResource(Res.string.file_links)
                            }
                        )
                    }
                )
            }
        }
    }
}