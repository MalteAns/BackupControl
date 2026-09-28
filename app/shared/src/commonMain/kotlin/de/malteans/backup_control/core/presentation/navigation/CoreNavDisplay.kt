package de.malteans.backup_control.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import de.malteans.backup_control.backup.presentation.BackupScreen

@Composable
fun CoreNavDisplay(
    modifier: Modifier = Modifier,
) {
    val backStack = remember { mutableStateListOf<Route>(Route.Backup) }

    NavDisplay(
        backStack = backStack,
        onBack = {},
        entryDecorators = listOf(
            rememberViewModelStoreNavEntryDecorator(),
        ),
        modifier = modifier,
        entryProvider = { key ->
            when (key) {
                is Route.Backup -> NavEntry(key) {
                    BackupScreen(
                        onClose = { backStack.removeLast() },
                    )
                }
                else -> error("Unknown route: $key")
            }
        }
    )
}