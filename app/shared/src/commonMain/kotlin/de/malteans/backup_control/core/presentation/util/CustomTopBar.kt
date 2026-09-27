package de.malteans.backup_control.core.presentation.util

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape

private val defaultShape
    @Composable get() = MaterialTheme.shapes.extraLarge.copy(
        topStart = CornerSize(0),
        topEnd = CornerSize(0),
    )

private val defaultColors
    @Composable get() = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
        titleContentColor = MaterialTheme.colorScheme.onPrimary,
        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
    )

@Composable
fun CustomTopBar(
    title: String,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = defaultColors,
    shape: Shape = defaultShape,
    modifier: Modifier = Modifier
) {
    CustomTopBar(
        title = { Text(text = title) },
        navigationIcon = navigationIcon,
        actions = actions,
        colors = colors,
        shape = shape,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = defaultColors,
    shape: Shape = defaultShape,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = title,
        actions = actions,
        navigationIcon = navigationIcon,
        colors = colors,
        modifier = modifier
            .clip(shape)
    )
}