package de.malteans.backup_control.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.backup_control.Endpoints
import de.malteans.backup_control.core.data.network.HttpStatusException
import de.malteans.backup_control.core.data.settings.ServerConfigRepository
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val serverConfigRepository: ServerConfigRepository,
    private val client: HttpClient,
): ViewModel() {
    companion object {
        private val SERVER_URL_REGEX = Regex("^https?://[\\w.-]+(:\\d+)?(/.*)?$")
    }

    private val _state = MutableStateFlow(SettingsState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = SettingsState()
        )

    init {
        viewModelScope.launch {
            val serverConfig = serverConfigRepository.getServerConfig()
            _state.update {
                it.copy(
                    isLoading = false,
                    serverUrl = serverConfig?.serverUrl ?: "",
                    apiToken = serverConfig?.apiToken ?: "",
                )
            }
        }
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.OnBack -> throw IllegalArgumentException("$action should be handled in RootScreen")
            is SettingsAction.ChangeServerUrl -> _state.update {
                it.copy(
                    serverUrl = action.url,
                    isServerUrlValid = action.url.isBlank() || isValidServerUrl(action.url),
                    isTestSuccessful = null,
                )
            }
            is SettingsAction.ChangeApiToken -> _state.update {
                it.copy(
                    apiToken = action.token,
                    isTestSuccessful = null,
                )
            }
            SettingsAction.ToggleTokenVisibility -> _state.update { it.copy(isTokenVisible = !it.isTokenVisible) }
            SettingsAction.Save -> save()
            SettingsAction.TestConnection -> testConnection()
        }
    }

    private fun save() {
        val currentState = _state.value
        if (!isValidServerUrl(currentState.serverUrl)) {
            _state.update { it.copy(isServerUrlValid = false) }
            return
        }

        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            serverConfigRepository.setServerUrl(currentState.serverUrl)
            serverConfigRepository.setApiToken(currentState.apiToken)
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun testConnection() {
        val currentState = _state.value
        if (!isValidServerUrl(currentState.serverUrl)) {
            _state.update { it.copy(isServerUrlValid = false) }
            return
        }

        _state.update { it.copy(isTestRunning = true, isTestSuccessful = null) }
        viewModelScope.launch(Dispatchers.IO) {
            val result = runCatching {
                val response = client.get(Endpoints.Backup.GetAll.url(currentState.serverUrl)) {
                    header("Authorization", "Bearer ${currentState.apiToken}")
                }
                if (response.status.isSuccess()) {
                    Result.success(Unit)
                } else {
                    Result.failure(HttpStatusException(
                        statusCode = response.status,
                        data = response.bodyAsText().ifBlank { null },
                    ))
                }
            }.getOrDefault(Result.failure(Exception("Could not execute request")))

            _state.update { it.copy(
                isTestRunning = false,
                isTestSuccessful = result.isSuccess,
            ) }
        }
    }

    private fun isValidServerUrl(url: String): Boolean = SERVER_URL_REGEX.matches(url.trim())
}
