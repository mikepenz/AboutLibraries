package com.mikepenz.aboutlibraries.ui.compose.style

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrariesDensity

/** Controls how much contrast emphasis is applied to badges, subtle text, and dividers. */
enum class ContrastLevel { Normal, High }

/**
 * Color tokens used by the new variant entry points (Traditional / Refined).
 *
 * Any field set to [Color.Unspecified] signals "fall back to a sensible default" — typically the
 * row content color or a semi-transparent overlay over the background.
 */
@Stable
interface VariantColors {
    val headerBackground: Color
    val headerOnBackground: Color
    val headerSubtleContent: Color
    val headerDivider: Color

    val rowBackground: Color
    val rowExpandedBackground: Color
    val rowOnBackground: Color
    val rowSubtleContent: Color
    val rowDivider: Color

    val actionFilledContainer: Color
    val actionFilledContent: Color
    val actionOutlineBorder: Color
    val actionOutlineContent: Color
    val actionLinkColor: Color

    val tabIdleBackground: Color
    val tabIdleContent: Color
    val tabActiveBackground: Color
    val tabActiveBorder: Color
    val tabActiveContent: Color

    val sheetScrim: Color
    val sheetSurface: Color
    val sheetSurfaceVariant: Color
    val sheetDragHandle: Color

    val licenseHueResolver: LicenseHueResolver
    val contrastLevel: ContrastLevel
}

@Immutable
class DefaultVariantColors(
    override val headerBackground: Color,
    override val headerOnBackground: Color,
    override val headerSubtleContent: Color,
    override val headerDivider: Color,
    override val rowBackground: Color,
    override val rowExpandedBackground: Color,
    override val rowOnBackground: Color,
    override val rowSubtleContent: Color,
    override val rowDivider: Color,
    override val actionFilledContainer: Color,
    override val actionFilledContent: Color,
    override val actionOutlineBorder: Color,
    override val actionOutlineContent: Color,
    override val actionLinkColor: Color,
    override val tabIdleBackground: Color,
    override val tabIdleContent: Color,
    override val tabActiveBackground: Color,
    override val tabActiveBorder: Color,
    override val tabActiveContent: Color,
    override val sheetScrim: Color,
    override val sheetSurface: Color,
    override val sheetSurfaceVariant: Color,
    override val sheetDragHandle: Color,
    override val licenseHueResolver: LicenseHueResolver,
    override val contrastLevel: ContrastLevel = ContrastLevel.Normal,
) : VariantColors

/**
 * Padding tokens for variant rows / headers / sheets / tabs / actions.
 */
@Stable
interface VariantPadding {
    val rowPaddingCozy: PaddingValues
    val rowPaddingCompact: PaddingValues
    val rowHorizontal: Dp
    val headerPadding: PaddingValues
    val sheetPadding: PaddingValues
    val tabPadding: PaddingValues
    val actionChipPadding: PaddingValues
    val actionLinkSpacing: Dp
    val inlineDetailPadding: PaddingValues
    val inlineActionsPadding: PaddingValues

    fun rowPaddingFor(density: LibrariesDensity): PaddingValues = when (density) {
        LibrariesDensity.Cozy -> rowPaddingCozy
        LibrariesDensity.Compact -> rowPaddingCompact
    }
}

@Immutable
class DefaultVariantPadding(
    override val rowPaddingCozy: PaddingValues,
    override val rowPaddingCompact: PaddingValues,
    override val rowHorizontal: Dp,
    override val headerPadding: PaddingValues,
    override val sheetPadding: PaddingValues,
    override val tabPadding: PaddingValues,
    override val actionChipPadding: PaddingValues,
    override val actionLinkSpacing: Dp,
    override val inlineDetailPadding: PaddingValues,
    override val inlineActionsPadding: PaddingValues,
) : VariantPadding

@Stable
interface VariantDimensions {
    val licenseDotSize: Dp
    val chevronSize: Dp
    val headerIconSize: Dp
    val searchHeight: Dp
    val sheetMaxHeightFraction: Float
    val sheetCornerRadius: Dp
    val actionIconSize: Dp
    val actionIconInnerSize: Dp
    val dragHandleWidth: Dp
    val dragHandleHeight: Dp
    val tabHeight: Dp
    val rowDividerThickness: Dp
}

@Immutable
class DefaultVariantDimensions(
    override val licenseDotSize: Dp,
    override val chevronSize: Dp,
    override val headerIconSize: Dp,
    override val searchHeight: Dp,
    override val sheetMaxHeightFraction: Float,
    override val sheetCornerRadius: Dp,
    override val actionIconSize: Dp,
    override val actionIconInnerSize: Dp,
    override val dragHandleWidth: Dp,
    override val dragHandleHeight: Dp,
    override val tabHeight: Dp,
    override val rowDividerThickness: Dp,
) : VariantDimensions

/**
 * Text style tokens. Both Traditional and Refined variants share these — field names are neutral
 * (e.g. [nameTextStyle] is used by both rows). Adapter modules supply non-null defaults.
 */
@Stable
interface VariantTextStyles {
    val nameTextStyle: TextStyle
    val authorTextStyle: TextStyle
    val versionTextStyle: TextStyle
    val licenseTextStyle: TextStyle
    val descriptionTextStyle: TextStyle

    val headerTitleTextStyle: TextStyle
    val headerTaglineTextStyle: TextStyle

    val tabTextStyle: TextStyle
    val tabCountTextStyle: TextStyle

    val sheetTitleTextStyle: TextStyle
    val sheetMetaTextStyle: TextStyle
    val sheetBodyTextStyle: TextStyle

    val actionLinkTextStyle: TextStyle
    val actionChipTextStyle: TextStyle
}

@Immutable
class DefaultVariantTextStyles(
    override val nameTextStyle: TextStyle,
    override val authorTextStyle: TextStyle,
    override val versionTextStyle: TextStyle,
    override val licenseTextStyle: TextStyle,
    override val descriptionTextStyle: TextStyle,
    override val headerTitleTextStyle: TextStyle,
    override val headerTaglineTextStyle: TextStyle,
    override val tabTextStyle: TextStyle,
    override val tabCountTextStyle: TextStyle,
    override val sheetTitleTextStyle: TextStyle,
    override val sheetMetaTextStyle: TextStyle,
    override val sheetBodyTextStyle: TextStyle,
    override val actionLinkTextStyle: TextStyle,
    override val actionChipTextStyle: TextStyle,
) : VariantTextStyles

@Stable
interface VariantShapes {
    val rowShape: Shape
    val headerSearchShape: Shape
    val tabShape: Shape
    val sheetShape: Shape
    val actionChipShape: Shape
    val licenseTokenShape: Shape
    val sheetLicenseShape: Shape
}

@Immutable
class DefaultVariantShapes(
    override val rowShape: Shape,
    override val headerSearchShape: Shape,
    override val tabShape: Shape,
    override val sheetShape: Shape,
    override val actionChipShape: Shape,
    override val licenseTokenShape: Shape,
    override val sheetLicenseShape: Shape,
) : VariantShapes

/**
 * Cohesive bundle of every variant token bag. Passed to public composables via a single param
 * (`style: LibrariesStyle`) so callers don't have to thread five separate bags through every call.
 */
@Immutable
class LibrariesStyle(
    val colors: VariantColors,
    val padding: VariantPadding,
    val dimensions: VariantDimensions,
    val textStyles: VariantTextStyles,
    val shapes: VariantShapes,
)

/** User-facing strings used by the variant headers / search field. Override for localization. */
@Immutable
class LibraryStrings(
    val searchPlaceholder: String = "Search libraries",
)

/** Labels shown on per-action affordances. Override for localization. */
@Immutable
class LibraryActionLabels(
    val source: String = "Source",
    val website: String = "Website",
    val sponsor: String = "Sponsor",
    val viewLicense: String = "View license",
)

val DefaultLibraryStrings: LibraryStrings = LibraryStrings()
val DefaultLibraryActionLabels: LibraryActionLabels = LibraryActionLabels()

/**
 * Theme-agnostic factory functions for the variant token bags.
 *
 * Color values default to [Color.Unspecified]; adapter modules (M3/M2/Wear) supply concrete
 * defaults via their own extension functions on [LibraryDefaults].
 */
@Composable
fun LibraryDefaults.defaultVariantColors(
    headerBackground: Color = Color.Unspecified,
    headerOnBackground: Color = Color.Unspecified,
    headerSubtleContent: Color = Color.Unspecified,
    headerDivider: Color = Color.Unspecified,
    rowBackground: Color = Color.Unspecified,
    rowExpandedBackground: Color = Color.Unspecified,
    rowOnBackground: Color = Color.Unspecified,
    rowSubtleContent: Color = Color.Unspecified,
    rowDivider: Color = Color.Unspecified,
    actionFilledContainer: Color = Color.Unspecified,
    actionFilledContent: Color = Color.Unspecified,
    actionOutlineBorder: Color = Color.Unspecified,
    actionOutlineContent: Color = Color.Unspecified,
    actionLinkColor: Color = Color.Unspecified,
    tabIdleBackground: Color = Color.Unspecified,
    tabIdleContent: Color = Color.Unspecified,
    tabActiveBackground: Color = Color.Unspecified,
    tabActiveBorder: Color = Color.Unspecified,
    tabActiveContent: Color = Color.Unspecified,
    sheetScrim: Color = Color.Unspecified,
    sheetSurface: Color = Color.Unspecified,
    sheetSurfaceVariant: Color = Color.Unspecified,
    sheetDragHandle: Color = Color.Unspecified,
    licenseHueResolver: LicenseHueResolver = LicenseHueResolver.None,
): VariantColors = remember(
    headerBackground, headerOnBackground, headerSubtleContent, headerDivider,
    rowBackground, rowExpandedBackground, rowOnBackground, rowSubtleContent, rowDivider,
    actionFilledContainer, actionFilledContent, actionOutlineBorder, actionOutlineContent, actionLinkColor,
    tabIdleBackground, tabIdleContent, tabActiveBackground, tabActiveBorder, tabActiveContent,
    sheetScrim, sheetSurface, sheetSurfaceVariant, sheetDragHandle,
    licenseHueResolver,
) {
    DefaultVariantColors(
        headerBackground, headerOnBackground, headerSubtleContent, headerDivider,
        rowBackground, rowExpandedBackground, rowOnBackground, rowSubtleContent, rowDivider,
        actionFilledContainer, actionFilledContent, actionOutlineBorder, actionOutlineContent, actionLinkColor,
        tabIdleBackground, tabIdleContent, tabActiveBackground, tabActiveBorder, tabActiveContent,
        sheetScrim, sheetSurface, sheetSurfaceVariant, sheetDragHandle,
        licenseHueResolver,
    )
}

fun LibraryDefaults.defaultVariantPadding(
    rowPaddingCozy: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
    rowPaddingCompact: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
    rowHorizontal: Dp = 20.dp,
    headerPadding: PaddingValues = PaddingValues(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 18.dp),
    sheetPadding: PaddingValues = PaddingValues(start = 22.dp, top = 20.dp, end = 22.dp, bottom = 28.dp),
    tabPadding: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
    actionChipPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
    actionLinkSpacing: Dp = 16.dp,
    inlineDetailPadding: PaddingValues = PaddingValues(start = 30.dp, top = 10.dp, end = 16.dp, bottom = 14.dp),
    inlineActionsPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
): VariantPadding = DefaultVariantPadding(
    rowPaddingCozy, rowPaddingCompact, rowHorizontal, headerPadding, sheetPadding, tabPadding,
    actionChipPadding, actionLinkSpacing, inlineDetailPadding, inlineActionsPadding,
)

fun LibraryDefaults.defaultVariantDimensions(
    licenseDotSize: Dp = 8.dp,
    chevronSize: Dp = 14.dp,
    headerIconSize: Dp = 48.dp,
    searchHeight: Dp = 44.dp,
    sheetMaxHeightFraction: Float = 0.8f,
    sheetCornerRadius: Dp = 28.dp,
    actionIconSize: Dp = 32.dp,
    actionIconInnerSize: Dp = 16.dp,
    dragHandleWidth: Dp = 32.dp,
    dragHandleHeight: Dp = 4.dp,
    tabHeight: Dp = 28.dp,
    rowDividerThickness: Dp = 1.dp,
): VariantDimensions = DefaultVariantDimensions(
    licenseDotSize, chevronSize, headerIconSize, searchHeight,
    sheetMaxHeightFraction, sheetCornerRadius,
    actionIconSize, actionIconInnerSize, dragHandleWidth, dragHandleHeight,
    tabHeight, rowDividerThickness,
)

private val FallbackTextStyle = TextStyle.Default

fun LibraryDefaults.defaultVariantTextStyles(
    nameTextStyle: TextStyle = FallbackTextStyle,
    authorTextStyle: TextStyle = FallbackTextStyle,
    versionTextStyle: TextStyle = FallbackTextStyle,
    licenseTextStyle: TextStyle = FallbackTextStyle,
    descriptionTextStyle: TextStyle = FallbackTextStyle,
    headerTitleTextStyle: TextStyle = FallbackTextStyle,
    headerTaglineTextStyle: TextStyle = FallbackTextStyle,
    tabTextStyle: TextStyle = FallbackTextStyle,
    tabCountTextStyle: TextStyle = FallbackTextStyle,
    sheetTitleTextStyle: TextStyle = FallbackTextStyle,
    sheetMetaTextStyle: TextStyle = FallbackTextStyle,
    sheetBodyTextStyle: TextStyle = FallbackTextStyle,
    actionLinkTextStyle: TextStyle = FallbackTextStyle,
    actionChipTextStyle: TextStyle = FallbackTextStyle,
): VariantTextStyles = DefaultVariantTextStyles(
    nameTextStyle, authorTextStyle, versionTextStyle, licenseTextStyle, descriptionTextStyle,
    headerTitleTextStyle, headerTaglineTextStyle,
    tabTextStyle, tabCountTextStyle,
    sheetTitleTextStyle, sheetMetaTextStyle, sheetBodyTextStyle,
    actionLinkTextStyle, actionChipTextStyle,
)

fun LibraryDefaults.defaultVariantShapes(
    rowShape: Shape = RoundedCornerShape(0.dp),
    headerSearchShape: Shape = RoundedCornerShape(22.dp),
    tabShape: Shape = RoundedCornerShape(CornerSize(percent = 50)),
    sheetShape: Shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
    actionChipShape: Shape = RoundedCornerShape(8.dp),
    licenseTokenShape: Shape = RoundedCornerShape(CornerSize(percent = 50)),
    sheetLicenseShape: Shape = RoundedCornerShape(16.dp),
): VariantShapes = DefaultVariantShapes(
    rowShape, headerSearchShape, tabShape, sheetShape, actionChipShape, licenseTokenShape, sheetLicenseShape,
)

/** Bundle the five token bags into a single [LibrariesStyle] holder, [remember]ed across recompositions. */
@Composable
fun LibraryDefaults.librariesStyle(
    colors: VariantColors,
    padding: VariantPadding = remember { defaultVariantPadding() },
    dimensions: VariantDimensions = remember { defaultVariantDimensions() },
    textStyles: VariantTextStyles = remember { defaultVariantTextStyles() },
    shapes: VariantShapes = remember { defaultVariantShapes() },
): LibrariesStyle = remember(colors, padding, dimensions, textStyles, shapes) {
    LibrariesStyle(colors, padding, dimensions, textStyles, shapes)
}
