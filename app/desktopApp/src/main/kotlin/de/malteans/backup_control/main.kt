package de.malteans.backup_control

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "BackupControl",
    ) {
        App()
    }
}