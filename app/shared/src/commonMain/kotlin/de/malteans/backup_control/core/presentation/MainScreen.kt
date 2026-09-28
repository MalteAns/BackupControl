package de.malteans.backup_control.core.presentation

import androidx.compose.runtime.Composable
import de.malteans.backup_control.core.presentation.navigation.CoreNavDisplay
import de.malteans.backup_control.core.presentation.theme.AppTheme

@Composable
fun MainScreen() {
    AppTheme {
        CoreNavDisplay()
    }
}
