package de.malteans.backup_control.core.presentation.util

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult

suspend fun SnackbarHostState.showSnackbar(
    message: UiText, actionLabel: String? = null,
    duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
    withDismissAction: Boolean = duration == SnackbarDuration.Indefinite,
    onAction: () -> Unit = {},
) {
    val result = this.showSnackbar(
        message = message.asStringAsync(),
        actionLabel = actionLabel,
        withDismissAction = withDismissAction,
        duration = duration
    )
    if (result == SnackbarResult.ActionPerformed) {
        onAction()
    }
}

suspend fun SnackbarHostState.showSnackbar(
    throwable: Throwable
) {
    this.showSnackbar(
        message = throwable.toUiText(),
        withDismissAction = true,
        duration = SnackbarDuration.Short,
    )
}
