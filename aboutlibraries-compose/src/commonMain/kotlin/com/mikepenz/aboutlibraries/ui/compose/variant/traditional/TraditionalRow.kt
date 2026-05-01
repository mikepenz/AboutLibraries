package com.mikepenz.aboutlibraries.ui.compose.variant.traditional

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.style.ContrastLevel
import com.mikepenz.aboutlibraries.ui.compose.style.LibrariesStyle
import com.mikepenz.aboutlibraries.ui.compose.style.VariantColors
import com.mikepenz.aboutlibraries.ui.compose.style.VariantShapes
import com.mikepenz.aboutlibraries.ui.compose.style.VariantTextStyles
import com.mikepenz.aboutlibraries.ui.compose.style.orFallback
import com.mikepenz.aboutlibraries.ui.compose.util.author
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrariesDensity
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryBadges

/**
 * Theme-agnostic Traditional row — the "by the book" Material 3 list item.
 *
 * Layout:
 *   Row 1: name (flex) · trailing version chip
 *   Row 2: author (subtle)
 *   Row 3: description (subtle, multi-line, when [LibraryBadges.description] is on)
 *   Row 4: license badges (FlowRow of pill chips)
 */
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun TraditionalRow(
    library: Library,
    expanded: Boolean,
    onToggle: () -> Unit,
    density: LibrariesDensity,
    badges: LibraryBadges,
    style: LibrariesStyle,
    modifier: Modifier = Modifier,
) {
    val colors = style.colors
    val onBg = colors.rowOnBackground.orFallback(Color.Black)
    val subtle = colors.rowSubtleContent.orFallback(onBg.copy(alpha = 0.6f))
    val rowBgState = animateColorAsState(
        targetValue = if (expanded) colors.rowExpandedBackground.orFallback(Color.Transparent)
            else colors.rowBackground.orFallback(Color.Transparent),
        animationSpec = tween(durationMillis = 200),
    )

    val firstLicense = remember(library) { library.licenses.firstOrNull() }
    val licenseHue = remember(library, colors.licenseHueResolver) {
        firstLicense?.spdxId?.let { colors.licenseHueResolver.colorFor(it) }
            ?: firstLicense?.name?.let { colors.licenseHueResolver.colorFor(it) }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind { drawRect(rowBgState.value) }
            .clickable(onClick = onToggle)
            .padding(style.padding.rowPaddingFor(density)),
        verticalArrangement = Arrangement.spacedBy(if (density == LibrariesDensity.Cozy) 4.dp else 2.dp),
    ) {
        // Row 1: name + version trailing
        Row(verticalAlignment = Alignment.Top) {
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
        }

        // Row 2: author
        val authorText = library.author.takeIf { it.isNotBlank() && badges.author }
        if (authorText != null) {
            BasicText(
                text = authorText,
                style = style.textStyles.authorTextStyle.copy(color = subtle),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        // Row 3: description
        val description = library.description?.takeIf { it.isNotBlank() && badges.description }
        if (description != null) {
            BasicText(
                text = description,
                style = style.textStyles.descriptionTextStyle.copy(color = subtle),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }

        // Row 4: license badges
        if (badges.license && library.licenses.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                library.licenses.forEach { license ->
                    val hue = license.spdxId?.let { colors.licenseHueResolver.colorFor(it) }
                        ?: license.name.let { colors.licenseHueResolver.colorFor(it) }
                        ?: licenseHue
                    LicenseBadge(
                        text = license.name,
                        hue = hue,
                        colors = colors,
                        shapes = style.shapes,
                        textStyles = style.textStyles,
                    )
                }
            }
        }
    }
}

@Composable
private fun LicenseBadge(
    text: String,
    hue: Color?,
    colors: VariantColors,
    shapes: VariantShapes,
    textStyles: VariantTextStyles,
) {
    val onBg = colors.rowOnBackground.orFallback(Color.Black)
    val badgeAlpha = if (colors.contrastLevel == ContrastLevel.High) 0.22f else 0.15f
    val container = (hue ?: colors.actionFilledContainer.orFallback(onBg)).copy(alpha = badgeAlpha)
    val content = hue ?: colors.actionFilledContent.orFallback(onBg)
    val borderMod = if (colors.contrastLevel == ContrastLevel.High)
        Modifier.border(1.dp, content.copy(alpha = 0.5f), shapes.licenseTokenShape)
    else Modifier
    Box(
        modifier = Modifier
            .clip(shapes.licenseTokenShape)
            .background(container)
            .then(borderMod)
            .padding(horizontal = 10.dp, vertical = 3.dp),
    ) {
        BasicText(text = text, style = textStyles.licenseTextStyle.copy(color = content), maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
