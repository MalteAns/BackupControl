package de.malteans.backup_control.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import de.malteans.backup_control.backup.presentation.BackupScreen
import de.malteans.backup_control.commandExecution.presentation.CommandExecutionScreenRoot
import de.malteans.backup_control.core.presentation.MainScreen
import de.malteans.backup_control.core.presentation.util.ErrorScreen

@Composable
fun CoreNavDisplay() {
    val backStack = remember { mutableStateListOf<Route>(Route.Main) }

    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.size > 1) backStack.removeLast()
        },
        entryDecorators = listOf(
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = { key ->
            when (key) {
                is Route.Backup -> NavEntry(key) {
                    BackupScreen(
                        onClose = { backStack.removeLast() },
                    )
                }
                is Route.CommandExecution -> NavEntry(key) {
                    CommandExecutionScreenRoot(
                        onBack = { backStack.removeLast() },
                    )
                }
                is Route.Main -> NavEntry(key) {
                    MainScreen(
                        onNavigate = { backStack.add(it) },
                    )
                }
                is Route.Settings -> NavEntry(key) {
                    ErrorScreen("Not implemented yet") // TODO: Implement Settings Screen
                }
                else -> error("Unknown route: $key")
            }
        }
    )
}