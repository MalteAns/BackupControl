package de.malteans.backup_control.core.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import backupcontrol.app.shared.generated.resources.Res
import backupcontrol.app.shared.generated.resources.app_name
import backupcontrol.app.shared.generated.resources.backups
import backupcontrol.app.shared.generated.resources.command_execution
import de.malteans.backup_control.core.presentation.navigation.Route
import de.malteans.backup_control.core.presentation.util.CustomTopBar
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainScreen(
    onNavigate: (Route) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(Res.string.app_name),
                actions = {
                    IconButton(onClick = { onNavigate(Route.Settings) }, enabled = false) { // TODO: Implement settings with server configuration
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 200.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(navigationButtons) { (title, route) ->
                Button(
                    onClick = { onNavigate(route) },
                ) {
                    Text(
                        text = stringResource(title),
                    )
                }
            }
        }
    }
}

data class NavigationButton(
    val title: StringResource,
    val route: Route,
)

private val navigationButtons = listOf(
    NavigationButton(
        title = Res.string.backups,
        route = Route.Backup,
    ),
    NavigationButton(
        title = Res.string.command_execution,
        route = Route.CommandExecution,
    )
)
