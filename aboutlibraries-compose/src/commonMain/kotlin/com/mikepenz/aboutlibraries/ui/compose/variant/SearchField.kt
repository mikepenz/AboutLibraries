package com.mikepenz.aboutlibraries.ui.compose.variant

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Theme-agnostic single-line search field.
 *
 * Stateful overload — internal query state survives configuration changes via [rememberSaveable].
 */
@Composable
fun DefaultSearchField(
    onQueryChange: (String) -> Unit,
    placeholder: String,
    contentColor: Color,
    placeholderColor: Color,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    initialQuery: String = "",
) {
    var query by rememberSaveable { mutableStateOf(initialQuery) }
    DefaultSearchField(
        query = query,
        onQueryChange = { query = it; onQueryChange(it) },
        placeholder = placeholder,
        contentColor = contentColor,
        placeholderColor = placeholderColor,
        textStyle = textStyle,
        modifier = modifier,
    )
}

/**
 * Stateless overload — caller hoists the [query] state.
 */
@Composable
fun DefaultSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    contentColor: Color,
    placeholderColor: Color,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        SearchGlyph(color = placeholderColor, size = 16.dp)
        Spacer(Modifier.width(10.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
            if (query.isEmpty()) {
                BasicText(text = placeholder, style = textStyle.copy(color = placeholderColor), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = textStyle.copy(color = contentColor),
                cursorBrush = SolidColor(contentColor),
            )
        }
    }
}

/** Pure-Compose magnifier glyph (foundation only, no Material icons). */
@Composable
private fun SearchGlyph(color: Color, size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val strokeWidth = (s / 12f).coerceAtLeast(1.5f)
        val stroke = Stroke(width = strokeWidth)
        val cx = s * 0.42f
        val cy = s * 0.42f
        val r = s * 0.30f
        drawCircle(color = color, radius = r, center = Offset(cx, cy), style = stroke)
        val handleStart = Offset(cx + r * 0.7f, cy + r * 0.7f)
        val handleEnd = Offset(s * 0.85f, s * 0.85f)
        drawLine(color, handleStart, handleEnd, strokeWidth = stroke.width, cap = StrokeCap.Round)
    }
}
