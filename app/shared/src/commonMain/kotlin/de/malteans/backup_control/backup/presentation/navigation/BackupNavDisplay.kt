package de.malteans.backup_control.backup.presentation.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import de.malteans.backup_control.backup.presentation.details.BackupDetailsScreenRoot
import de.malteans.backup_control.backup.presentation.overview.BackupOverviewScreenRoot
import de.malteans.backup_control.core.data.AnsLog
import de.malteans.backup_control.core.presentation.navigation.Route

private const val TAG = "BackupsNavDisplay"

@Composable
fun BackupNavDisplay(
    backStack: SnapshotStateList<Route>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        backStack = backStack,
        onBack = onBack,
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
        modifier = modifier,
        entryProvider = { key ->
            when (key) {
                is Route.Backup.Overview -> NavEntry(key) {
                    BackupOverviewScreenRoot(
                        onBackupClick = { logFileName ->
                            AnsLog.d(TAG, "Navigating to backup details for log file: $logFileName")
                            backStack.add(Route.Backup.Details(logFileName))
                        }
                    )
                }
                is Route.Backup.Details -> NavEntry(key) {
                    BackupDetailsScreenRoot(
                        logFileName = key.logFileName,
                    )
                }
                else -> error("Unknown route: $key")
            }
        }
    )
}