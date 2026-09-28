package de.malteans.backup_control.backup.presentation.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun BackupDetailsScreenRoot(
    logFileName: String,
    viewModel: BackupDetailsViewModel = koinViewModel {
        parametersOf(logFileName)
    }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (state.error != null) {
            Text(
                text = state.error ?: "Unknown error",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            BackupDetailsScreen(
                state = state,
                onAction = { action ->
                    when (action) {
                        else -> viewModel.onAction(action)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalGridApi::class)
@Composable
fun BackupDetailsScreen(
    state: BackupDetailsState,
    onAction: (BackupDetailsAction) -> Unit,
) {
    ElevatedCard(

    ) {
        Grid(
            config = {
                column(3.dp)
            }
        ) {

        }
    }
}
