package com.mikepenz.aboutlibraries.ui.compose.m3.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LibraryChip(
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
    border: BorderStroke? = null,
    containerColor: Color = MaterialTheme.colorScheme.onSurface
        .copy(alpha = SurfaceOverlayOpacity)
        .compositeOver(MaterialTheme.colorScheme.surface),
    contentColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentOpacity),
    minHeight: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .clip(shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = ripple(),
                        onClick = onClick
                    )
                } else Modifier
            )
            .semantics { role = Role.Button },
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        border = border,
    ) {
        ProvideTextStyle(value = MaterialTheme.typography.labelLarge) {
            Row(
                Modifier.defaultMinSize(minHeight = minHeight),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                content()
            }
        }
    }
}

private const val ContentOpacity = 0.87f
private const val SurfaceOverlayOpacity = 0.12f