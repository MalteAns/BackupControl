package de.malteans.backup_control

import androidx.compose.ui.window.ComposeUIViewController
import de.malteans.backup_control.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }