package de.malteans.backup_control.backup.presentation.overview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.backup_control.backup.presentation.components.BackupItem
import de.malteans.backup_control.core.presentation.util.CircularLoadingScreen
import de.malteans.backup_control.core.presentation.util.ErrorScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BackupOverviewScreenRoot(
    viewModel: BackupOverviewViewModel = koinViewModel(),
    onBackupClick: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BackupOverviewScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is BackupOverviewAction.OpenBackupLog -> {
                    onBackupClick(action.logFileName)
                }
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
fun BackupOverviewScreen(
    state: BackupOverviewState,
    onAction: (BackupOverviewAction) -> Unit,
) {
    if (state.isLoading) {
        CircularLoadingScreen()
        return
    }
    if (state.error != null) {
        ErrorScreen(state.error)
        return
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 300.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(state.backups) { backup ->
                BackupItem(
                    backup = backup,
                    onClick = { onAction(BackupOverviewAction.OpenBackupLog(backup.fileName)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

