package de.malteans.backup_control.core.presentation.util

import backupcontrol.app.shared.generated.resources.Res
import backupcontrol.app.shared.generated.resources.server_error_desc
import backupcontrol.app.shared.generated.resources.server_not_started_desc
import backupcontrol.app.shared.generated.resources.unexpected_error_desc
import de.malteans.backup_control.core.data.network.HttpStatusException

fun Throwable.toUiText() = when (this) {
    is HttpStatusException -> when (this.statusCode.value) {
        502 -> UiText.Resource(Res.string.server_not_started_desc)
        in 500..599 -> UiText.Resource(Res.string.server_error_desc, this.statusCode.value)
        else -> UiText.Resource(Res.string.unexpected_error_desc, this.statusCode.value)
    }
    else -> UiText.DynamicString(this.message ?: this::class.simpleName ?: "Unknown error")
}