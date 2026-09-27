package de.malteans.backup_control.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import de.malteans.backup_control.backups.presentation.details.BackupDetailsScreenRoot
import de.malteans.backup_control.backups.presentation.overview.BackupsOverviewScreenRoot

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
) {
    val backStack = remember { mutableStateListOf<AppRoute>(BackupsRoute) }

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberViewModelStoreNavEntryDecorator(),
        ),
        modifier = modifier,
        entryProvider = { key ->
            when (key) {
                is BackupsRoute -> NavEntry(key) {
                    BackupsOverviewScreenRoot(
                        onBackupClick = { logFileName ->
                            backStack.add(BackupDetailRoute(logFileName))
                        }
                    )
                }
                is BackupDetailRoute -> NavEntry(key) {
                    BackupDetailsScreenRoot(
                        logFileName = key.logFileName,
                    )
                }
                else -> error("Unknown route: $key")
            }
        }
    )
}