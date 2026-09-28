package de.malteans.backup_control.backup.presentation.overview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.backup_control.backup.presentation.util.BackupItem
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
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (state.error != null) {
            Text(
                text = state.error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 300.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
            ) {
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
}

