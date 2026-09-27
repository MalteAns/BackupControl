package de.malteans.backup_control.backups.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import de.malteans.backup_control.backups.presentation.navigation.BackupNavDisplay
import de.malteans.backup_control.core.presentation.navigation.Route
import de.malteans.backup_control.core.presentation.util.CustomTopBar

@Composable
fun BackupScreen(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStack = rememberSaveable { mutableStateListOf<Route>(Route.Backup.Overview) }

    val onBack: () -> Unit =  {
        if (backStack.size > 1) backStack.removeLast()
        else onClose()
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "Backups",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        BackupNavDisplay(
            backStack = backStack,
            onBack = onBack,
            modifier = Modifier.padding(paddingValues)
        )
    }
}