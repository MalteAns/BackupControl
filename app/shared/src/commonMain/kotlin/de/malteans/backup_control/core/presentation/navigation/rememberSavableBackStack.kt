package de.malteans.backup_control.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.serialization.json.Json

@Composable
fun rememberSavableBackStack(
    vararg initialStack: Route,
) = rememberSaveable(
    saver = listSaver(
        save = { stateList -> stateList.map { Json.encodeToString(it) } },
        restore = { savedList ->
            mutableStateListOf<Route>().apply {
                savedList.forEach { str ->
                    add(Json.decodeFromString<Route>(str))
                }
            }
        }
    )
) { mutableStateListOf<Route>(*initialStack) }