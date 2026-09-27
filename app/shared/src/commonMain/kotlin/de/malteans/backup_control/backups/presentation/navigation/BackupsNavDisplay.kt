package de.malteans.backup_control.backups.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import de.malteans.backup_control.backups.presentation.details.BackupDetailsScreenRoot
import de.malteans.backup_control.backups.presentation.overview.BackupsOverviewScreenRoot
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
        modifier = modifier,
        entryProvider = { key ->
            when (key) {
                is Route.Backup.Overview -> NavEntry(key) {
                    BackupsOverviewScreenRoot(
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