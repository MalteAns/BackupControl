package de.malteans.backup_control.commandExecution.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import backupcontrol.app.shared.generated.resources.Res
import backupcontrol.app.shared.generated.resources.command_execution
import backupcontrol.app.shared.generated.resources.navigate_back
import de.malteans.backup_control.commandExecution.presentation.components.CommandItem
import de.malteans.backup_control.core.presentation.util.CustomTopBar
import de.malteans.backup_control.core.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CommandExecutionScreenRoot(
    onBack: () -> Unit,
    viewModel: CommandExecutionViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is CommandExecutionEvent.ShowSnackbar -> {
                // TODO
            }
            else -> throw NotImplementedError("Event '${event::class.simpleName}' not implemented")
        }
    }

    CommandExecutionScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is CommandExecutionAction.OnBack -> onBack()
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
fun CommandExecutionScreen(
    state: CommandExecutionState,
    onAction: (CommandExecutionAction) -> Unit,
) {

    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(Res.string.command_execution),
                navigationIcon = {
                    IconButton(onClick = { onAction(CommandExecutionAction.OnBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.navigate_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(250.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(Modifier.height(8.dp))
            }
            items(state.commands) { command ->
                CommandItem(
                    command = command,
                    onClick = { onAction(CommandExecutionAction.ExecuteCommand(command.uuid)) },
                )
            }
        }
    }
}

