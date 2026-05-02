package com.mikepenz.aboutlibraries.ui.compose.variant

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.style.LibrariesStyle
import com.mikepenz.aboutlibraries.ui.compose.style.LibraryActionLabels
import com.mikepenz.aboutlibraries.ui.compose.style.orFallback

/**
 * Theme-agnostic action affordance bar.
 *
 * Used by inline-expand and the sheet body.
 */
@Composable
fun LibraryActions(
    library: Library,
    actionMode: LibraryActionMode,
    style: LibrariesStyle,
    modifier: Modifier = Modifier,
    actionLabels: LibraryActionLabels,
    onActionClick: ((Library, LibraryActionKind) -> Unit)? = null,
) {
    val uriHandler = LocalUriHandler.current
    val source = library.scm?.url
    val website = library.website
    val sponsor = library.funding.firstOrNull()?.url
    val licenseUrl = library.licenses.firstOrNull()?.url
    val licensePresent = library.licenses.firstOrNull() != null

    val open = remember(library, onActionClick, uriHandler) {
        { kind: LibraryActionKind, url: String? -> dispatchAction(library, kind, url, onActionClick, uriHandler) }
    }

    when (actionMode) {
        LibraryActionMode.Chips -> Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!source.isNullOrBlank()) ActionChip(actionLabels.source, filled = false, style) { open(LibraryActionKind.Source, source) }
            if (!website.isNullOrBlank()) ActionChip(actionLabels.website, filled = false, style) { open(LibraryActionKind.Website, website) }
            if (!sponsor.isNullOrBlank()) ActionChip(actionLabels.sponsor, filled = false, style) { open(LibraryActionKind.Sponsor, sponsor) }
            if (licensePresent) ActionChip(actionLabels.viewLicense, filled = true, style) { open(LibraryActionKind.License, licenseUrl) }
        }

        LibraryActionMode.Icons -> Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!source.isNullOrBlank()) ActionIconButton(ActionIcon.Source, actionLabels.source, style) { open(LibraryActionKind.Source, source) }
            if (!website.isNullOrBlank()) ActionIconButton(ActionIcon.Website, actionLabels.website, style) { open(LibraryActionKind.Website, website) }
            if (!sponsor.isNullOrBlank()) ActionIconButton(ActionIcon.Sponsor, actionLabels.sponsor, style) { open(LibraryActionKind.Sponsor, sponsor) }
            if (licensePresent) ActionIconButton(ActionIcon.License, actionLabels.viewLicense, style) { open(LibraryActionKind.License, licenseUrl) }
        }

        LibraryActionMode.Links -> Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(style.padding.actionLinkSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!source.isNullOrBlank()) ActionLink(actionLabels.source, style) { open(LibraryActionKind.Source, source) }
            if (!website.isNullOrBlank()) ActionLink(actionLabels.website, style) { open(LibraryActionKind.Website, website) }
            if (!sponsor.isNullOrBlank()) ActionLink(actionLabels.sponsor, style) { open(LibraryActionKind.Sponsor, sponsor) }
            if (licensePresent) ActionLink(actionLabels.viewLicense, style) { open(LibraryActionKind.License, licenseUrl) }
        }
    }
}

private fun dispatchAction(
    library: Library,
    kind: LibraryActionKind,
    url: String?,
    onActionClick: ((Library, LibraryActionKind) -> Unit)?,
    uriHandler: UriHandler,
) {
    if (onActionClick != null) {
        onActionClick(library, kind)
    } else if (!url.isNullOrBlank()) {
        // Surface errors to a caller-installed handler if any; otherwise rethrow so the host
        // can decide. Silent swallow hides URI-handling bugs in production.
        uriHandler.openUri(url)
    }
}

@Composable
private fun ActionChip(
    label: String,
    filled: Boolean,
    style: LibrariesStyle,
    onClick: () -> Unit,
) {
    val colors = style.colors
    val container = if (filled) colors.actionFilledContainer.orFallback(Color.Transparent) else Color.Transparent
    val content = if (filled) colors.actionFilledContent.orFallback(Color.Black) else colors.actionOutlineContent.orFallback(Color.Black)
    val border = if (filled) Color.Transparent else colors.actionOutlineBorder.orFallback(content.copy(alpha = 0.4f))

    Box(
        modifier = Modifier
            .clip(style.shapes.actionChipShape)
            .background(container)
            .border(width = if (filled) 0.dp else 1.dp, color = border, shape = style.shapes.actionChipShape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(style.padding.actionChipPadding),
    ) {
        BasicText(text = label, style = style.textStyles.actionChipTextStyle.copy(color = content))
    }
}

@Composable
private fun ActionIconButton(
    icon: ActionIcon,
    contentDescription: String,
    style: LibrariesStyle,
    onClick: () -> Unit,
) {
    val colors = style.colors
    val border = colors.actionOutlineBorder.orFallback(colors.actionOutlineContent.orFallback(Color.Black).copy(alpha = 0.4f))
    val content = colors.actionOutlineContent.orFallback(Color.Black)
    val cd = contentDescription
    Box(
        modifier = Modifier
            .size(style.dimensions.actionIconSize)
            .clip(CircleShape)
            .border(1.dp, border, CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics {
                this.role = Role.Button
                this.contentDescription = cd
            },
        contentAlignment = Alignment.Center,
    ) {
        ActionIconGlyph(icon = icon, color = content, size = style.dimensions.actionIconInnerSize)
    }
}

@Composable
private fun ActionLink(label: String, style: LibrariesStyle, onClick: () -> Unit) {
    BasicText(
        text = label,
        modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
        style = style.textStyles.actionLinkTextStyle.copy(
            color = style.colors.actionLinkColor.orFallback(style.colors.rowOnBackground.orFallback(Color.Black)),
            textDecoration = TextDecoration.Underline,
        ),
    )
}

private enum class ActionIcon { Source, Website, Sponsor, License }

@Composable
private fun ActionIconGlyph(icon: ActionIcon, color: Color, size: androidx.compose.ui.unit.Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val strokeWidth = (s / 12f).coerceAtLeast(1.25f)
        val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (icon) {
            ActionIcon.Source -> drawSourceGlyph(s, stroke, color)
            ActionIcon.Website -> drawWebsiteGlyph(s, stroke, color)
            ActionIcon.Sponsor -> drawHeartGlyph(s, color)
            ActionIcon.License -> drawDocumentGlyph(s, stroke, color)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSourceGlyph(s: Float, stroke: Stroke, color: Color) {
    // Two angle-bracket strokes "< / >" representing source code.
    val pad = s * 0.18f
    val mid = s / 2f
    val topY = pad
    val botY = s - pad
    val left = pad
    val right = s - pad
    val midX = s / 2f
    drawLine(color, Offset(midX - s * 0.12f, topY), Offset(left, mid), strokeWidth = stroke.width, cap = stroke.cap)
    drawLine(color, Offset(left, mid), Offset(midX - s * 0.12f, botY), strokeWidth = stroke.width, cap = stroke.cap)
    drawLine(color, Offset(midX + s * 0.12f, topY), Offset(right, mid), strokeWidth = stroke.width, cap = stroke.cap)
    drawLine(color, Offset(right, mid), Offset(midX + s * 0.12f, botY), strokeWidth = stroke.width, cap = stroke.cap)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawWebsiteGlyph(s: Float, stroke: Stroke, color: Color) {
    val r = s * 0.4f
    val c = Offset(s / 2f, s / 2f)
    drawCircle(color = color, radius = r, center = c, style = stroke)
    // Equator + meridian strokes — simple globe.
    drawLine(color, Offset(c.x - r, c.y), Offset(c.x + r, c.y), strokeWidth = stroke.width)
    drawLine(color, Offset(c.x, c.y - r), Offset(c.x, c.y + r), strokeWidth = stroke.width)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHeartGlyph(s: Float, color: Color) {
    val path = Path().apply {
        val w = s
        val h = s
        moveTo(w * 0.5f, h * 0.85f)
        cubicTo(w * 0.05f, h * 0.6f, w * 0.05f, h * 0.2f, w * 0.5f, h * 0.35f)
        cubicTo(w * 0.95f, h * 0.2f, w * 0.95f, h * 0.6f, w * 0.5f, h * 0.85f)
        close()
    }
    drawPath(path = path, color = color)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDocumentGlyph(s: Float, stroke: Stroke, color: Color) {
    val pad = s * 0.22f
    val rectSize = Size(s - pad * 2f, s - pad * 1.6f)
    val origin = Offset(pad, pad * 0.8f)
    drawRect(color = color, topLeft = origin, size = rectSize, style = stroke)
    // Two horizontal lines representing text rules.
    val y1 = origin.y + rectSize.height * 0.4f
    val y2 = origin.y + rectSize.height * 0.65f
    drawLine(color, Offset(origin.x + s * 0.08f, y1), Offset(origin.x + rectSize.width - s * 0.08f, y1), strokeWidth = stroke.width)
    drawLine(color, Offset(origin.x + s * 0.08f, y2), Offset(origin.x + rectSize.width - s * 0.08f, y2), strokeWidth = stroke.width)
}
