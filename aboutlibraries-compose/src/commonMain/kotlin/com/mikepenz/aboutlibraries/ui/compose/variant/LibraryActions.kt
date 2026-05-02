package com.mikepenz.aboutlibraries.ui.compose.variant

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
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
import com.mikepenz.aboutlibraries.ui.compose.style.LibraryActionBadges
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
    actionLabels: LibraryActionBadges,
    onActionClick: ((Library, LibraryActionKind) -> Boolean)? = null,
    onLicenseContentRequest: ((Library) -> Unit)? = null,
) {
    val uriHandler = LocalUriHandler.current
    val source = library.scm?.url?.takeIf { actionLabels.sourceEnabled }
    val website = library.website?.takeIf { actionLabels.websiteEnabled }
    val sponsor = library.funding.firstOrNull()?.url?.takeIf { actionLabels.sponsorEnabled }
    val licenseUrl = library.licenses.firstOrNull()?.url
    val licensePresent = actionLabels.licenseEnabled && library.licenses.firstOrNull() != null

    val open = remember(library, onActionClick, onLicenseContentRequest, uriHandler) {
        { kind: LibraryActionKind, url: String? -> dispatchAction(library, kind, url, onActionClick, onLicenseContentRequest, uriHandler) }
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
            if (!source.isNullOrBlank()) ActionIconButton(actionLabels.sourceIcon, actionLabels.source, style) { open(LibraryActionKind.Source, source) }
            if (!website.isNullOrBlank()) ActionIconButton(actionLabels.websiteIcon, actionLabels.website, style) { open(LibraryActionKind.Website, website) }
            if (!sponsor.isNullOrBlank()) ActionIconButton(actionLabels.sponsorIcon, actionLabels.sponsor, style) { open(LibraryActionKind.Sponsor, sponsor) }
            if (licensePresent) ActionIconButton(actionLabels.viewLicenseIcon, actionLabels.viewLicense, style) { open(LibraryActionKind.License, licenseUrl) }
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
    onActionClick: ((Library, LibraryActionKind) -> Boolean)?,
    onLicenseContentRequest: ((Library) -> Unit)?,
    uriHandler: UriHandler,
) {
    val consumed = onActionClick?.invoke(library, kind) ?: false
    if (!consumed) {
        if (!url.isNullOrBlank()) {
            uriHandler.openUri(url)
        } else if (kind == LibraryActionKind.License && onLicenseContentRequest != null) {
            onLicenseContentRequest(library)
        }
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
    icon: ImageVector,
    contentDescription: String,
    style: LibrariesStyle,
    onClick: () -> Unit,
) {
    val colors = style.colors
    val border = colors.actionOutlineBorder.orFallback(colors.actionOutlineContent.orFallback(Color.Black).copy(alpha = 0.4f))
    val content = colors.actionOutlineContent.orFallback(Color.Black)
    val iconShape = style.shapes.actionIconShape
    val cd = contentDescription
    Box(
        modifier = Modifier
            .size(style.dimensions.actionIconSize)
            .clip(iconShape)
            .border(1.dp, border, iconShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics {
                this.role = Role.Button
                this.contentDescription = cd
            },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = rememberVectorPainter(icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(content),
            modifier = Modifier.size(style.dimensions.actionIconInnerSize),
        )
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
