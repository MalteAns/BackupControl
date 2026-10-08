package de.malteans.backup_control.commandExecution.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import backupcontrol.app.shared.generated.resources.Res
import backupcontrol.app.shared.generated.resources.command_execution
import backupcontrol.app.shared.generated.resources.navigate_back
import backupcontrol.app.shared.generated.resources.no_commands_found
import de.malteans.backup_control.commandExecution.presentation.components.CommandItem
import de.malteans.backup_control.core.presentation.util.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CommandExecutionScreenRoot(
    onBack: () -> Unit,
    viewModel: CommandExecutionViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is CommandExecutionEvent.ShowSnackbar -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        event.message,
                        event.actionLabel,
                        event.duration,
                        event.withDismissAction,
                        event.onAction,
                    )
                }
            }
            else -> throw NotImplementedError("Event '${event::class.simpleName}' not implemented")
        }
    }

    CommandExecutionScreen(
        state = state,
        snackbarHostState = snackbarHostState,
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
    snackbarHostState: SnackbarHostState,
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
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .padding(bottom = 48.dp)
            )
        },
    ) { innerPadding ->
        if (state.isLoading) {
            CircularLoadingScreen()
            return@Scaffold
        }
        if (state.error != null) {
            ErrorScreen(state.error.asString())
            return@Scaffold
        }
        if (state.commands.isEmpty()) {
            Box(Modifier.fillMaxSize()) {
                Text(
                    text = stringResource(Res.string.no_commands_found),
                )
            }
            return@Scaffold
        }

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

