package de.malteans.backup_control.core.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.malteans.backup_control.core.presentation.navigation.NavGraph

@Composable
fun MainScreen() {

    Scaffold(

    ) { paddingValues ->
        NavGraph(
            modifier = Modifier.padding(paddingValues)
        )
    }
}
