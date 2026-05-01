package com.mikepenz.aboutlibraries.ui.compose.variant.refined

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.style.LibrariesStyle
import com.mikepenz.aboutlibraries.ui.compose.style.VariantDimensions
import com.mikepenz.aboutlibraries.ui.compose.style.orFallback
import com.mikepenz.aboutlibraries.ui.compose.util.author
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrariesDensity
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryBadges

/**
 * Theme-agnostic Refined row.
 *
 * Layout:
 *   L1: license dot · name (flex) · version · chevron
 *   L2: author · separator · license-label
 */
@Composable
fun RefinedRow(
    library: Library,
    expanded: Boolean,
    onToggle: () -> Unit,
    density: LibrariesDensity,
    badges: LibraryBadges,
    style: LibrariesStyle,
    modifier: Modifier = Modifier,
) {
    val colors = style.colors
    val firstLicense = remember(library) { library.licenses.firstOrNull() }
    val licenseHue = remember(library, colors.licenseHueResolver) {
        firstLicense?.spdxId?.let { colors.licenseHueResolver.colorFor(it) }
            ?: firstLicense?.name?.let { colors.licenseHueResolver.colorFor(it) }
    }
    val rowBg = if (expanded) colors.rowExpandedBackground.orFallback(Color.Transparent)
    else colors.rowBackground.orFallback(Color.Transparent)
    val onBg = colors.rowOnBackground.orFallback(Color.Black)
    val subtle = colors.rowSubtleContent.orFallback(onBg.copy(alpha = 0.6f))

    val layoutDir = LocalLayoutDirection.current
    val rowPad = style.padding.rowPaddingFor(density)
    val startPad = remember(rowPad, layoutDir, style.dimensions.licenseDotSize) {
        rowPad.calculateStartPadding(layoutDir) + style.dimensions.licenseDotSize + 10.dp
    }
    val endPad = remember(rowPad, layoutDir) { rowPad.calculateEndPadding(layoutDir) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(rowBg)
            .clickable(onClick = onToggle),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(rowPad),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(style.dimensions.licenseDotSize)
                    .clip(CircleShape)
                    .background(licenseHue ?: subtle.copy(alpha = 0.4f)),
            )
            Spacer(Modifier.width(10.dp))
            BasicText(
                text = library.name,
                modifier = Modifier.weight(1f),
                style = style.textStyles.nameTextStyle.copy(color = onBg),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val version = library.artifactVersion
            if (badges.version && !version.isNullOrBlank()) {
                Spacer(Modifier.width(8.dp))
                BasicText(
                    text = version,
                    style = style.textStyles.versionTextStyle.copy(color = subtle, fontFamily = FontFamily.Monospace),
                    maxLines = 1,
                )
            }
            Spacer(Modifier.width(6.dp))
            ChevronGlyph(
                color = subtle,
                size = style.dimensions.chevronSize,
                rotationDegrees = if (expanded) 180f else 0f,
            )
        }

        // Second line: author · license
        val authorText = library.author.takeIf { it.isNotBlank() && badges.author }
        val licenseText = firstLicense?.name?.takeIf { badges.license }
        if (authorText != null || licenseText != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = startPad, end = endPad, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (authorText != null) {
                    BasicText(
                        text = authorText,
                        modifier = Modifier.weight(1f, fill = false),
                        style = style.textStyles.authorTextStyle.copy(color = subtle),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (authorText != null && licenseText != null) {
                    BasicText(
                        text = " · ",
                        style = style.textStyles.authorTextStyle.copy(color = subtle.copy(alpha = 0.5f)),
                    )
                }
                if (licenseText != null) {
                    BasicText(
                        text = licenseText,
                        style = style.textStyles.licenseTextStyle.copy(color = licenseHue ?: subtle),
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

/** Theme-independent fallback divider — used when adapters don't provide a custom one. */
@Composable
fun RefinedRowDivider(dividerColor: Color, dimensions: VariantDimensions) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensions.rowDividerThickness)
            .background(dividerColor),
    )
}

@Composable
private fun ChevronGlyph(color: Color, size: Dp, rotationDegrees: Float) {
    Canvas(modifier = Modifier.size(size).rotate(rotationDegrees)) {
        val s = this.size.minDimension
        val strokeWidth = (s / 8f).coerceAtLeast(1.5f)
        val left = Offset(s * 0.2f, s * 0.4f)
        val mid = Offset(s * 0.5f, s * 0.7f)
        val right = Offset(s * 0.8f, s * 0.4f)
        drawLine(color, left, mid, strokeWidth = strokeWidth, cap = StrokeCap.Round)
        drawLine(color, mid, right, strokeWidth = strokeWidth, cap = StrokeCap.Round)
    }
}
