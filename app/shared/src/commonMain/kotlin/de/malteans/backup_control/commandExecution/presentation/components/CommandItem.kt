package de.malteans.backup_control.commandExecution.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import backupcontrol.app.shared.generated.resources.Res
import backupcontrol.app.shared.generated.resources.unnamed_command
import de.malteans.backup_control.commandExecution.domain.Command
import org.jetbrains.compose.resources.stringResource

@Composable
fun CommandItem(
    command: Command,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier,
    ) {
        val contentColor = LocalContentColor.current
        ListItem(
            headlineContent = {
                Text(
                    text = command.name?.takeIf { it.isNotBlank() }
                        ?: stringResource(Res.string.unnamed_command)
                )
            },
            supportingContent = {
                Text(
                    text = command.command,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                    ),
                    maxLines = 2,
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
                contentColor = contentColor,
                supportingContentColor = contentColor,
            )
        )
    }
}