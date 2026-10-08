package de.malteans.backup_control.backup.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import backupcontrol.app.shared.generated.resources.Res
import backupcontrol.app.shared.generated.resources.backups
import de.malteans.backup_control.backup.presentation.navigation.BackupNavDisplay
import de.malteans.backup_control.core.presentation.navigation.Route
import de.malteans.backup_control.core.presentation.navigation.rememberSavableBackStack
import de.malteans.backup_control.core.presentation.util.CustomTopBar
import org.jetbrains.compose.resources.stringResource

@Composable
fun BackupScreen(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStack = rememberSavableBackStack(Route.Backup.Overview)

    val onBack: () -> Unit =  {
        if (backStack.size > 1) backStack.removeLast()
        else onClose()
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(Res.string.backups),
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