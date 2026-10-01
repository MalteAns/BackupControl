package de.malteans.backup_control.backup.presentation.components

import backupcontrol.app.shared.generated.resources.Res
import backupcontrol.app.shared.generated.resources.hours_unit
import backupcontrol.app.shared.generated.resources.minutes_unit
import backupcontrol.app.shared.generated.resources.seconds_unit
import de.malteans.backup_control.core.presentation.util.UiText

fun Int.formatDuration(): UiText {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60

    fun Int.toTwoDigits(): String = this.toString().padStart(2, '0')

    return when {
        hours > 0 -> UiText.Resource(Res.string.hours_unit, "${hours.toTwoDigits()}:${minutes.toTwoDigits()}:${seconds.toTwoDigits()}")
        minutes > 0 -> UiText.Resource(Res.string.minutes_unit, "${minutes.toTwoDigits()}:${seconds.toTwoDigits()}")
        else -> UiText.Resource(Res.string.seconds_unit, seconds.toTwoDigits())
    }
}