package de.malteans.backup_control

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import de.malteans.backup_control.di.initKoin

fun main() = application {
    initKoin()

    Window(
        onCloseRequest = ::exitApplication,
        title = "BackupControl",
    ) {
        App()
    }
}