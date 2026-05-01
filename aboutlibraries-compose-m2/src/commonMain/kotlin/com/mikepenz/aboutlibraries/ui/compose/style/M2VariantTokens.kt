package com.mikepenz.aboutlibraries.ui.compose.style

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults

/**
 * Builds Material 2 defaults for the variant color tokens.
 */
@Composable
fun LibraryDefaults.m2VariantColors(
    contrastLevel: ContrastLevel = ContrastLevel.Normal,
    headerBackground: Color = MaterialTheme.colors.surface,
    headerOnBackground: Color = MaterialTheme.colors.onSurface,
    headerSubtleContent: Color = MaterialTheme.colors.onSurface.copy(alpha = if (contrastLevel == ContrastLevel.High) 0.87f else 0.60f),
    headerDivider: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
    rowBackground: Color = MaterialTheme.colors.background,
    rowExpandedBackground: Color = MaterialTheme.colors.surface,
    rowOnBackground: Color = MaterialTheme.colors.onBackground,
    rowSubtleContent: Color = MaterialTheme.colors.onBackground.copy(alpha = if (contrastLevel == ContrastLevel.High) 0.87f else 0.60f),
    rowDivider: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
    actionFilledContainer: Color = MaterialTheme.colors.primary,
    actionFilledContent: Color = MaterialTheme.colors.onPrimary,
    actionOutlineBorder: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.38f),
    actionOutlineContent: Color = MaterialTheme.colors.onSurface,
    actionLinkColor: Color = MaterialTheme.colors.primary,
    tabIdleBackground: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
    tabIdleContent: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.60f),
    tabActiveBackground: Color = MaterialTheme.colors.primary.copy(alpha = 0.16f),
    tabActiveBorder: Color = MaterialTheme.colors.primary,
    tabActiveContent: Color = MaterialTheme.colors.primary,
    sheetScrim: Color = Color.Black.copy(alpha = 0.32f),
    sheetSurface: Color = MaterialTheme.colors.surface,
    sheetSurfaceVariant: Color = MaterialTheme.colors.surface,
    sheetDragHandle: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.24f),
    licenseHueResolver: LicenseHueResolver = DefaultM2LicenseHueResolver,
): VariantColors = remember(
    headerBackground, headerOnBackground, headerSubtleContent, headerDivider,
    rowBackground, rowExpandedBackground, rowOnBackground, rowSubtleContent, rowDivider,
    actionFilledContainer, actionFilledContent, actionOutlineBorder, actionOutlineContent, actionLinkColor,
    tabIdleBackground, tabIdleContent, tabActiveBackground, tabActiveBorder, tabActiveContent,
    sheetScrim, sheetSurface, sheetSurfaceVariant, sheetDragHandle, licenseHueResolver, contrastLevel,
) {
    DefaultVariantColors(
        headerBackground, headerOnBackground, headerSubtleContent, headerDivider,
        rowBackground, rowExpandedBackground, rowOnBackground, rowSubtleContent, rowDivider,
        actionFilledContainer, actionFilledContent, actionOutlineBorder, actionOutlineContent, actionLinkColor,
        tabIdleBackground, tabIdleContent, tabActiveBackground, tabActiveBorder, tabActiveContent,
        sheetScrim, sheetSurface, sheetSurfaceVariant, sheetDragHandle, licenseHueResolver,
        contrastLevel = contrastLevel,
    )
}

/**
 * Builds Material 2 typography defaults for the variant text styles.
 */
@Composable
fun LibraryDefaults.m2VariantTextStyles(
    nameTextStyle: TextStyle = MaterialTheme.typography.subtitle2.copy(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 1.2.em,
    ),
    authorTextStyle: TextStyle = MaterialTheme.typography.caption.copy(
        fontSize = 11.sp,
        lineHeight = 1.25.em,
    ),
    versionTextStyle: TextStyle = MaterialTheme.typography.overline.copy(fontSize = 11.sp),
    licenseTextStyle: TextStyle = MaterialTheme.typography.overline.copy(
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
    ),
    descriptionTextStyle: TextStyle = MaterialTheme.typography.body2.copy(
        fontSize = 12.sp,
        lineHeight = 1.5.em,
    ),
    headerTitleTextStyle: TextStyle = MaterialTheme.typography.h6.copy(
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.2).sp,
        lineHeight = 1.1.em,
    ),
    headerTaglineTextStyle: TextStyle = MaterialTheme.typography.body1.copy(fontSize = 13.sp),
    tabTextStyle: TextStyle = MaterialTheme.typography.overline.copy(
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
    ),
    tabCountTextStyle: TextStyle = MaterialTheme.typography.overline.copy(fontSize = 10.sp),
    sheetTitleTextStyle: TextStyle = MaterialTheme.typography.h5.copy(
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeightStyle = LineHeightStyle.Default,
    ),
    sheetMetaTextStyle: TextStyle = MaterialTheme.typography.body1.copy(fontSize = 13.sp),
    sheetBodyTextStyle: TextStyle = MaterialTheme.typography.body1.copy(
        fontSize = 14.sp,
        lineHeight = 1.5.em,
    ),
    actionLinkTextStyle: TextStyle = MaterialTheme.typography.button.copy(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
    ),
    actionChipTextStyle: TextStyle = MaterialTheme.typography.button.copy(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
    ),
): VariantTextStyles = remember(
    nameTextStyle, authorTextStyle, versionTextStyle, licenseTextStyle, descriptionTextStyle,
    headerTitleTextStyle, headerTaglineTextStyle,
    tabTextStyle, tabCountTextStyle,
    sheetTitleTextStyle, sheetMetaTextStyle, sheetBodyTextStyle,
    actionLinkTextStyle, actionChipTextStyle,
) {
    DefaultVariantTextStyles(
        nameTextStyle, authorTextStyle, versionTextStyle, licenseTextStyle, descriptionTextStyle,
        headerTitleTextStyle, headerTaglineTextStyle,
        tabTextStyle, tabCountTextStyle,
        sheetTitleTextStyle, sheetMetaTextStyle, sheetBodyTextStyle,
        actionLinkTextStyle, actionChipTextStyle,
    )
}

/** Singleton resolver over [DarkM3LicensePalette] reused for M2 — same hue identities. */
val DefaultM2LicenseHueResolver: LicenseHueResolver = LicenseHueResolver(
    mapOf(
        "Apache-2.0" to Color(0xFFB69CFF),
        "MIT" to Color(0xFF7AC0FF),
        "EPL-2.0" to Color(0xFFE4A56A),
        "EPL-1.0" to Color(0xFFE4A56A),
        "BSD-3-Clause" to Color(0xFF8AD4A4),
        "BSD-2-Clause" to Color(0xFF8AD4A4),
        "GPL-3.0" to Color(0xFFFF8E8E),
        "GPL-3.0-only" to Color(0xFFFF8E8E),
        "GPL-3.0-or-later" to Color(0xFFFF8E8E),
        "GPL-2.0" to Color(0xFFFF8E8E),
        "LGPL-2.1" to Color(0xFFFFB088),
        "LGPL-3.0" to Color(0xFFFFB088),
        "MPL-2.0" to Color(0xFFFFD27A),
        "ISC" to Color(0xFF7AC0FF),
        "Unlicense" to Color(0xFFB7B7B7),
        "CC0-1.0" to Color(0xFFB7B7B7),
    )
)
