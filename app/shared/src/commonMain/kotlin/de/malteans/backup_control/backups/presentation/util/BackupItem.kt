package de.malteans.backup_control.backups.presentation.util

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.malteans.backup_control.backups.domain.Backup
import kotlinx.datetime.LocalDateTime

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
            headlineContent = { Text(backup.datetime.toNiceString()) },
            supportingContent = { Text(backup.fileName) },
//            leadingContent = {
//                Icon(
//                    imageVector =
//                )
//            },
//            trailingContent = {
//                Icon(
//                    imageVector =
//                )
//            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
                contentColor = LocalContentColor.current,
            ),
        )
    }
}

fun LocalDateTime.toNiceString(): String {
    return "${this.date} ${this.time}"
}
