package com.mikepenz.aboutlibraries.sample.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Custom pill toggle from the sample-app design. 44×24, 20dp circular knob.
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
    val borderColor = if (on) androidx.compose.ui.graphics.Color.Transparent
    else MaterialTheme.colorScheme.outlineVariant

    val knobOffsetX = if (on) width - knob - 2.dp else 2.dp

    Box(
        modifier = modifier
            .size(width, height)
            .clip(RoundedCornerShape(height / 2))
            .background(if (on) onColor else offColor)
            .border(1.dp, borderColor, RoundedCornerShape(height / 2))
            .clickable(onClick = onToggle),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset(x = knobOffsetX, y = 0.dp)
                .size(knob)
                .clip(CircleShape)
                .background(if (on) knobOnBg else knobOffBg),
            contentAlignment = Alignment.Center,
        ) {
            when {
                textIndicator != null -> Text(
                    text = textIndicator,
                    color = if (on) knobOnFg else knobOffFg,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                )
                icon != null -> Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = if (on) knobOnFg else knobOffFg,
                    modifier = Modifier.size(11.dp),
                )
            }
        }
    }
}
