package de.malteans.backup_control.backup.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import backupcontrol.app.shared.generated.resources.*
import de.malteans.backup_control.model.Backup
import org.jetbrains.compose.resources.stringResource

@Composable
fun BackupItem(
    backup: Backup,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        onClick = onClick,
        enabled = backup.success != null,
        colors = when (backup.success) {
            false -> CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
            else -> CardDefaults.elevatedCardColors()
        },
        modifier = modifier
    ) {
        ListItem(
            headlineContent = { Text(backup.startTime?.date.toString()) },
            supportingContent = {
                Text(
                    text = "${stringResource(Res.string.duration)}: ${backup.duration?.formatDuration() ?: "—"}",
                )
            },
            leadingContent = {
                Icon(
                    imageVector = when (backup.success) {
                        true -> Icons.Outlined.CheckCircle
                        false -> Icons.Outlined.ErrorOutline
                        null -> Icons.Outlined.HourglassEmpty
                    },
                    contentDescription = null
                )
            },
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = null
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
                contentColor = LocalContentColor.current,
                leadingContentColor = LocalContentColor.current,
                trailingContentColor = LocalContentColor.current,
            ),
        )
    }
}

@Composable
private fun Int.formatDuration(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60

    fun Int.toTwoDigits(): String = this.toString().padStart(2, '0')

    return when {
        hours > 0 -> "${hours.toTwoDigits()}:${minutes.toTwoDigits()}:${seconds.toTwoDigits()} ${stringResource(Res.string.hours)}"
        minutes > 0 -> "${minutes.toTwoDigits()}:${seconds.toTwoDigits()} ${stringResource(Res.string.minutes)}"
        else -> "${seconds.toTwoDigits()} ${stringResource(Res.string.seconds)}"
    }
}
