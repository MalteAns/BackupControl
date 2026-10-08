package de.malteans.backup_control.core.presentation.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import de.malteans.backup_control.backup.presentation.BackupScreen
import de.malteans.backup_control.commandExecution.presentation.CommandExecutionScreenRoot
import de.malteans.backup_control.core.data.AnsLog
import de.malteans.backup_control.core.presentation.MainScreen
import de.malteans.backup_control.core.presentation.util.ErrorScreen

private const val TAG = "CoreNavDisplay"

@Composable
fun CoreNavDisplay() {
    val backStack = rememberSavableBackStack(Route.Main)

    NavDisplay(
        backStack = backStack,
        onBack = {
            AnsLog.d(TAG, "NavDisplay::onBack - called with back stack size: ${backStack.size}")
            if (backStack.size > 1) backStack.removeLast()
        },
        entryDecorators = listOf(
            rememberViewModelStoreNavEntryDecorator(),
        ),
        transitionSpec = {
            slideInHorizontally(initialOffsetX = { it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { -it })
        },
        popTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        },
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