package de.malteans.backup_control.backups.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import de.malteans.backup_control.backups.presentation.util.BackupItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BackupsScreenRoot(
    viewModel: BackupViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BackupsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is BackupsAction.OpenBackupLog -> {
                    TODO()
                }
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
fun BackupsScreen(
    state: BackupsState,
    onAction: (BackupsAction) -> Unit,
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
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.backups) { backup ->
                    BackupItem(
                        backup = backup,
                        onClick = { onAction(BackupsAction.OpenBackupLog(backup.uuid)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

