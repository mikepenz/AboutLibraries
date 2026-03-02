package com.mikepenz.aboutlibraries.ui.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library


/**
 * Contains the default values used by [Library]
 */
object LibraryDefaults {
    /**
     * Creates a [LibraryPadding] that represents the default paddings used in a [Library]
     *
     * @param contentPadding the padding inside the [Library] ui element
     * @param namePadding the padding around the name shown as part of a [Library]
     * @param versionPadding the padding in and around the version shown as part of a [Library]
     * @param licensePadding the padding in and around the license shown as part of a [Library]
     * @param fundingPadding the padding in and around the funding shown as part of a [Library]
     * @param verticalPadding the vertical padding between the individual items in the library element
     */
    @Composable
    fun libraryPadding(
        contentPadding: PaddingValues = PaddingValues(16.dp),
        namePadding: PaddingValues = PaddingValues(0.dp),
        versionPadding: ChipPadding = chipPadding(
            containerPadding = PaddingValues(start = 8.dp),
        ),
        licensePadding: ChipPadding = chipPadding(),
        fundingPadding: ChipPadding = chipPadding(),
        verticalPadding: Dp = 2.dp,
        licenseDialogContentPadding: Dp = 8.dp,
    ): LibraryPadding = DefaultLibraryPadding(
        contentPadding = contentPadding,
        namePadding = namePadding,
        versionPadding = versionPadding,
        licensePadding = licensePadding,
        fundingPadding = fundingPadding,
        verticalPadding = verticalPadding,
        licenseDialogContentPadding = licenseDialogContentPadding,
    )

    /**
     * Creates a ChipPadding that represents the default paddings used in a chip in a [Library].
     *
     * @param containerPadding the padding around the Chip UI Element
     * @param contentPadding the padding inside the Chip UI element
     */
    @Composable
    fun chipPadding(
        containerPadding: PaddingValues = PaddingValues(top = 8.dp, end = 4.dp),
        contentPadding: PaddingValues = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
    ): ChipPadding = DefaultChipPadding(
        containerPadding = containerPadding,
        contentPadding = contentPadding,
    )

    /**
     * Creates a [LibraryDimensions] that represents the default dimensions used in a [Library]
     *
     * @param itemSpacing the spacing between items in the [Library]
     * @param chipMinHeight the default min height of chip containers in the [Library]
     */
    @Composable
    fun libraryDimensions(
        itemSpacing: Dp = 0.dp,
        chipMinHeight: Dp = 16.dp,
    ): LibraryDimensions = DefaultLibraryDimensions(
        itemSpacing = itemSpacing,
        chipMinHeight = chipMinHeight
    )

    /**
     * Creates a [LibraryTextStyles] that represents the default configurations used in a [Library]
     *
     * @param defaultOverflow the default text overflow for all line limited texts
     * @param nameTextStyle the text styles for the name text
     * @param nameMaxLines the max lines allowed for the name text
     * @param nameOverflow the text overflow for the name text
     * @param versionTextStyle the text styles for the version text
     * @param versionMaxLines the max lines allowed for the version text
     * @param authorTextStyle the text styles for the author text
     * @param authorMaxLines the max lines allowed for the author text
     * @param descriptionTextStyle the text styles for the description text
     * @param descriptionMaxLines the max lines allowed for the description text
     * @param licensesTextStyle the text styles for the licenses text
     */
    @Composable
    fun libraryTextStyles(
        defaultOverflow: TextOverflow = TextOverflow.Ellipsis,
        nameTextStyle: TextStyle? = null,
        nameMaxLines: Int = 1,
        nameOverflow: TextOverflow = defaultOverflow,
        versionTextStyle: TextStyle? = null,
        versionMaxLines: Int = nameMaxLines,
        authorTextStyle: TextStyle? = null,
        authorMaxLines: Int = nameMaxLines,
        descriptionTextStyle: TextStyle? = null,
        descriptionMaxLines: Int = 3,
        licensesTextStyle: TextStyle? = null,
        fundingTextStyle: TextStyle? = null,
    ): LibraryTextStyles = DefaultLibraryTextStyles(
        defaultOverflow = defaultOverflow,
        nameTextStyle = nameTextStyle,
        nameMaxLines = nameMaxLines,
        nameOverflow = nameOverflow,
        versionTextStyle = versionTextStyle,
        versionMaxLines = versionMaxLines,
        authorTextStyle = authorTextStyle,
        authorMaxLines = authorMaxLines,
        descriptionTextStyle = descriptionTextStyle,
        descriptionMaxLines = descriptionMaxLines,
        licensesTextStyle = licensesTextStyle,
        fundingTextStyle = fundingTextStyle,
    )

    /**
     * Creates a [LibraryShapes] that represents the default shapes used in a [Library]
     */
    @Composable
    fun libraryShapes(
        chipShape: Shape = RoundedCornerShape(CornerSize(percent = 50)),
    ): LibraryShapes = DefaultLibraryShapes(
        chipShape = chipShape,
    )
}

/**
 * Represents the background and content colors used in a library.
 */
@Stable
interface LibraryColors {
    /** Represents the background color for this library item. */
    val libraryBackgroundColor: Color

    /** Represents the content color for this library item. */
    val libraryContentColor: Color

    /** Represents the colors used for the [Library.artifactVersion] chip. */
    val versionChipColors: ChipColors

    /** Represents the colors used for the [Library.licenses] chip. */
    val licenseChipColors: ChipColors

    /** Represents the colors used for the [Library.funding] chip. */
    val fundingChipColors: ChipColors

    /** Represents the background color of the dialog. */
    val dialogBackgroundColor: Color

    /** Represents the content color of the dialog. */
    val dialogContentColor: Color

    /** Represents the text color of the dialog's confirm button  */
    val dialogConfirmButtonColor: Color
}

/**
 * Default [LibraryColors].
 */
@Immutable
class DefaultLibraryColors(
    override val libraryBackgroundColor: Color,
    override val libraryContentColor: Color,
    override val versionChipColors: ChipColors,
    override val licenseChipColors: ChipColors,
    override val fundingChipColors: ChipColors,
    override val dialogBackgroundColor: Color,
    override val dialogContentColor: Color,
    override val dialogConfirmButtonColor: Color,
) : LibraryColors

/** Represents the color values used for a chip.*/
@Stable
interface ChipColors {
    /** Represents the background color of the Chip */
    val containerColor: Color

    /** Represents the color inside the Chip UI element (for the text) */
    val contentColor: Color
}

/**
 * Default [ChipColors].
 */
@Immutable
class DefaultChipColors(
    override val containerColor: Color,
    override val contentColor: Color,
) : ChipColors

/**
 * Represents the padding values used in a library.
 */
@Stable
interface LibraryPadding {
    /** Represents the padding inside the [Library] ui element */
    val contentPadding: PaddingValues

    /** Represents the padding around the name shown as part of a [Library] */
    val namePadding: PaddingValues

    /** Represents the padding values used for the chip containing the [Library.artifactVersion] */
    val versionPadding: ChipPadding

    /** Represents the padding values used for the chip containing the [Library.licenses] */
    val licensePadding: ChipPadding

    /** Represents the padding values used for the chip containing the [Library.funding] funding */
    val fundingPadding: ChipPadding

    /** Represents the vertical padding between the individual items in the library element */
    val verticalPadding: Dp

    /** Represents the padding used for the content in the license dialog. */
    val licenseDialogContentPadding: Dp
}

/**
 * Default [LibraryPadding].
 */
@Immutable
private class DefaultLibraryPadding(
    override val contentPadding: PaddingValues,
    override val namePadding: PaddingValues,
    override val versionPadding: ChipPadding,
    override val licensePadding: ChipPadding,
    override val fundingPadding: ChipPadding,
    override val verticalPadding: Dp,
    override val licenseDialogContentPadding: Dp,
) : LibraryPadding

/** Represents the padding values used for a chip.*/
@Stable
interface ChipPadding {
    /** Represents the padding around the Chip UI Element */
    val containerPadding: PaddingValues

    /** Represents the padding inside the Chip UI element */
    val contentPadding: PaddingValues
}

/**
 * Default [ChipPadding].
 */
@Immutable
private class DefaultChipPadding(
    override val containerPadding: PaddingValues,
    override val contentPadding: PaddingValues,
) : ChipPadding

/**
 * Represents the padding values used in a library.
 */
@Stable
interface LibraryDimensions {
    /** Represents the spacing between items in the [Library] */
    val itemSpacing: Dp

    /** Represents the default min height of chip containers in the [Library] */
    val chipMinHeight: Dp
}

/**
 * Default [LibraryDimensions].
 */
@Immutable
private class DefaultLibraryDimensions(
    override val itemSpacing: Dp,
    override val chipMinHeight: Dp,
) : LibraryDimensions

/**
 * Represents the text styles used in a library.
 */
@Stable
interface LibraryTextStyles {
    /** Represents the default text overflow for all line limited texts */
    val defaultOverflow: TextOverflow

    /** Represents the text styles for the name text */
    val nameTextStyle: TextStyle?

    /** Represents the max lines allowed for the name text */
    val nameMaxLines: Int

    /** Represents the text overflow for the name text */
    val nameOverflow: TextOverflow

    /** Represents the text styles for the version text */
    val versionTextStyle: TextStyle?

    /** Represents the max lines allowed for the version text */
    val versionMaxLines: Int

    /** Represents the text styles for the author text */
    val authorTextStyle: TextStyle?

    /** Represents the max lines allowed for the author text */
    val authorMaxLines: Int

    /** Represents the text styles for the description text */
    val descriptionTextStyle: TextStyle?

    /** Represents the max lines allowed for the description text */
    val descriptionMaxLines: Int

    /** Represents the text styles for the licenses badge text */
    val licensesTextStyle: TextStyle?

    /** Represents the text styles for the funding badge text */
    val fundingTextStyle: TextStyle?
}

/**
 * Default [LibraryTextStyles].
 */
@Immutable
private class DefaultLibraryTextStyles(
    override val defaultOverflow: TextOverflow,
    override val nameTextStyle: TextStyle?,
    override val nameMaxLines: Int,
    override val nameOverflow: TextOverflow,
    override val versionTextStyle: TextStyle?,
    override val versionMaxLines: Int,
    override val authorTextStyle: TextStyle?,
    override val authorMaxLines: Int,
    override val descriptionTextStyle: TextStyle?,
    override val descriptionMaxLines: Int,
    override val licensesTextStyle: TextStyle?,
    override val fundingTextStyle: TextStyle?,
) : LibraryTextStyles


/**
 * Represents the shape used for chips in a library.
 */
@Stable
interface LibraryShapes {
    /** The [Shape] used for chips. */
    val chipShape: Shape
}

/**
 * Default [LibraryShapes].
 */
@Immutable
private class DefaultLibraryShapes(
    override val chipShape: Shape,
) : LibraryShapes
