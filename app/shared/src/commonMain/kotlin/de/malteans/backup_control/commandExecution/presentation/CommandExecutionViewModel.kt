package de.malteans.backup_control.commandExecution.presentation

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import backupcontrol.app.shared.generated.resources.Res
import backupcontrol.app.shared.generated.resources.command_executed_successfully
import de.malteans.backup_control.commandExecution.domain.CommandRemoteSource
import de.malteans.backup_control.core.data.settings.ServerConfigRepository
import de.malteans.backup_control.core.data.settings.ServerNotConfiguredException
import de.malteans.backup_control.core.presentation.util.UiText
import de.malteans.backup_control.core.presentation.util.toUiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CommandExecutionViewModel(
    private val remoteSource: CommandRemoteSource,
    private val serverConfigRepository: ServerConfigRepository,
): ViewModel() {

    private val _state = MutableStateFlow(CommandExecutionState())
    private val eventChannel = Channel<CommandExecutionEvent>()
    val events = eventChannel.receiveAsFlow()

    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = CommandExecutionState()
        )

    init {
        viewModelScope.launch {
            serverConfigRepository.getServerConfigFlow().collect {
                loadCommands()
            }
        }
    }

    fun onAction(action: CommandExecutionAction) {
        when (action) {
            is CommandExecutionAction.ExecuteCommand -> {
                viewModelScope.launch(Dispatchers.IO) {
                    remoteSource.executeCommand(action.uuid)
                        .onSuccess {
                            sendEvent(CommandExecutionEvent.ShowSnackbar(
                                message = UiText.Resource(Res.string.command_executed_successfully),
                                withDismissAction = true,
                            ))
                        }
                        .onFailure { error ->
                            sendEvent(CommandExecutionEvent.ShowSnackbar(
                                message = error.toUiText(),
                                duration = SnackbarDuration.Long,
                                withDismissAction = true,
                            ))
                        }
                }
            }
            else -> throw NotImplementedError()
        }
    }

    private fun loadCommands() {
        _state.update { it.copy(
            isLoading = true,
        ) }
        viewModelScope.launch {
            remoteSource.getCommands()
                .onSuccess { commands ->
                    _state.update { it.copy(
                        isLoading = false,
                        error = null,

                        commands = commands
                    ) }
                }
                .onFailure { error ->
                    // TODO: Log
                    _state.update { it.copy(
                        isLoading = false,
                        error = error.toUiText(),
                        isServerNotConfigured = error is ServerNotConfiguredException,
                    ) }
                }
        }
    }

    private fun sendEvent(event: CommandExecutionEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}