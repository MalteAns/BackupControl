package de.malteans.backup_control.settings.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import backupcontrol.app.shared.generated.resources.*
import de.malteans.backup_control.core.presentation.util.CircularLoadingScreen
import de.malteans.backup_control.core.presentation.util.CustomTopBar
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreenRoot(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is SettingsAction.OnBack -> onBack()
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
) {
    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(Res.string.settings),
                navigationIcon = {
                    IconButton(onClick = { onAction(SettingsAction.OnBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.navigate_back)
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        if (state.isLoading) {
            CircularLoadingScreen()
            return@Scaffold
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.serverUrl,
                onValueChange = { onAction(SettingsAction.ChangeServerUrl(it)) },
                label = { Text(stringResource(Res.string.server_url)) },
                placeholder = { Text(stringResource(Res.string.server_url_placeholder)) },
                isError = !state.isServerUrlValid,
                supportingText = {
                    if (!state.isServerUrlValid) {
                        Text(stringResource(Res.string.invalid_server_url))
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.apiToken,
                onValueChange = { onAction(SettingsAction.ChangeApiToken(it)) },
                label = { Text(stringResource(Res.string.api_token)) },
                visualTransformation = if (state.isTokenVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(autoCorrect = false),
                trailingIcon = {
                    IconButton(onClick = { onAction(SettingsAction.ToggleTokenVisibility) }) {
                        Icon(
                            imageVector = if (state.isTokenVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = null,
                        )
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = { onAction(SettingsAction.Save) },
                    enabled = !state.isSaving,
                ) {
                    Text(stringResource(Res.string.save))
                }
                OutlinedButton(
                    onClick = { onAction(SettingsAction.TestConnection) },
                    enabled = !state.isTestRunning,
                ) {
                    Text(stringResource(Res.string.test_connection))
                }
                if (state.isTestSuccessful != null) {
                    Icon(
                        imageVector = if (state.isTestSuccessful == true) {
                            Icons.Default.Check
                        } else {
                            Icons.Default.Close
                        },
                        contentDescription = null,
                        tint = if (state.isTestSuccessful == true) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
