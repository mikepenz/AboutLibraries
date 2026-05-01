package com.mikepenz.aboutlibraries.sample.sample

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * M3-style segmented control with a sliding pill indicator.
 *
 * Outer container: surfaceContainerHighest, radius 10dp, padding 4dp.
 * Active pill: primary fill slides with a spring; text colors cross-fade via tween.
 */
@Composable
fun <T> Segmented(
    options: List<Pair<T, String>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pillShape = RoundedCornerShape(7.dp)
    val spacingDp = 6.dp
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val onSurface = MaterialTheme.colorScheme.onSurface

    val n = options.size
    val selectedIndex = options.indexOfFirst { it.first == selected }.coerceAtLeast(0)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(10.dp))
            .padding(4.dp),
    ) {
        // Pill width = (available width minus all gaps) / number of segments.
        val pillWidthDp = (maxWidth - spacingDp * (n - 1)) / n
        val pillOffsetState = animateDpAsState(
            targetValue = (pillWidthDp + spacingDp) * selectedIndex,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium,
            ),
        )
        val density = LocalDensity.current

        // Box with IntrinsicSize.Min so the pill can fillMaxHeight() to match the Row.
        Box(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            // Sliding pill — rendered first so it sits behind the labels.
            Box(
                modifier = Modifier
                    .offset { IntOffset(with(density) { pillOffsetState.value.roundToPx() }, 0) }
                    .width(pillWidthDp)
                    .fillMaxHeight()
                    .background(primary, pillShape),
            )

            // Labels row — on top of pill, provides the height for the outer Box.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacingDp),
            ) {
                options.forEach { (key, label) ->
                    val active = key == selected
                    val textColor by animateColorAsState(
                        targetValue = if (active) onPrimary else onSurface,
                        animationSpec = tween(durationMillis = 150),
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelect(key) }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            color = textColor,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}
