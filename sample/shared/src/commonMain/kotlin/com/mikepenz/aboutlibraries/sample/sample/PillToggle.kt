package com.mikepenz.aboutlibraries.sample.sample

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Custom pill toggle from the sample-app design. 44×24, 20dp circular knob.
 * Knob slides via spring; track and knob colors cross-fade via tween.
 */
@Composable
fun PillToggle(
    on: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    textIndicator: String? = null,
    contentDescription: String? = null,
) {
    val width = 44.dp
    val height = 24.dp
    val knob = 20.dp

    val onColor = MaterialTheme.colorScheme.primaryContainer
    val offColor = MaterialTheme.colorScheme.surfaceContainerHighest
    val knobOnBg = MaterialTheme.colorScheme.primary
    val knobOffBg = MaterialTheme.colorScheme.outline
    val knobOnFg = MaterialTheme.colorScheme.onPrimary
    val knobOffFg = MaterialTheme.colorScheme.surface
    val borderOnColor = androidx.compose.ui.graphics.Color.Transparent
    val borderOffColor = MaterialTheme.colorScheme.outlineVariant

    val colorSpec = tween<androidx.compose.ui.graphics.Color>(durationMillis = 200)
    val trackColor by animateColorAsState(if (on) onColor else offColor, animationSpec = colorSpec)
    val knobBg by animateColorAsState(if (on) knobOnBg else knobOffBg, animationSpec = colorSpec)
    val knobFg by animateColorAsState(if (on) knobOnFg else knobOffFg, animationSpec = colorSpec)
    val borderColor by animateColorAsState(if (on) borderOnColor else borderOffColor, animationSpec = colorSpec)

    // Spring: medium stiffness with a tiny bit of bounce for a physical feel.
    val knobOffsetState = animateDpAsState(
        targetValue = if (on) width - knob - 2.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
    )
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .size(width, height)
            .clip(RoundedCornerShape(height / 2))
            .toggleable(value = on, onValueChange = { onToggle() }, role = Role.Switch)
            .semantics { if (contentDescription != null) this.contentDescription = contentDescription!! }
            .background(trackColor)
            .border(1.dp, borderColor, RoundedCornerShape(height / 2)),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            // Lambda offset defers the read to Layout phase (performance skill).
            modifier = Modifier
                .offset { IntOffset(with(density) { knobOffsetState.value.roundToPx() }, 0) }
                .size(knob)
                .background(knobBg, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            when {
                textIndicator != null -> BasicText(
                    text = textIndicator,
                    style = TextStyle(
                        color = knobFg,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.Both,
                        ),
                    ),
                )
                icon != null -> Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = knobFg,
                    modifier = Modifier.size(11.dp),
                )
            }
        }
    }
}
