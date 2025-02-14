package com.mikepenz.aboutlibraries.ui.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
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
     * @param namePadding the padding around the name shown as part of a [Library]
     * @param versionPadding the padding around the version shown as part of a [Library]
     * @param badgePadding the padding around a badge element shown as part of a [Library]
     * @param badgeContentPadding the padding around the content of a badge element shown as part of a [Library]
     * @param verticalPadding the vertical padding between the individual items in the library element
     */
    @Composable
    fun libraryPadding(
        contentPadding: PaddingValues = PaddingValues(16.dp),
        namePadding: PaddingValues = PaddingValues(0.dp),
        versionPadding: PaddingValues = PaddingValues(start = 8.dp),
        badgePadding: PaddingValues = PaddingValues(top = 8.dp, end = 4.dp),
        badgeContentPadding: PaddingValues = PaddingValues(0.dp),
        verticalPadding: Dp = 2.dp,
    ): LibraryPadding = DefaultLibraryPadding(
        contentPadding = contentPadding,
        namePadding = namePadding,
        versionPadding = versionPadding,
        badgePadding = badgePadding,
        badgeContentPadding = badgeContentPadding,
        verticalPadding = verticalPadding,
    )

    /**
     * Creates a [LibraryDimensions] that represents the default dimensions used in a [Library]
     *
     * @param itemSpacing the spacing between items in the [Library]
     */
    fun libraryDimensions(
        itemSpacing: Dp = 0.dp,
    ): LibraryDimensions = DefaultLibraryDimensions(
        itemSpacing = itemSpacing,
    )

    /**
     * Creates a [LibraryTextStyles] that represents the default configurations used in a [Library]
     *
     * @param defaultOverflow the default text overflow for all line limited texts
     * @param nameMaxLines the max lines allowed for the name text
     * @param nameOverflow the text overflow for the name text
     * @param versionMaxLines the max lines allowed for the version text
     * @param authorMaxLines the max lines allowed for the author text
     * @param descriptionMaxLines the max lines allowed for the description text
     */
    fun libraryTextStyles(
        defaultOverflow: TextOverflow = TextOverflow.Ellipsis,
        nameTextStyles: TextStyle? = null,
        nameMaxLines: Int = 1,
        nameOverflow: TextOverflow = defaultOverflow,
        versionTextStyle: TextStyle? = null,
        versionMaxLines: Int = nameMaxLines,
        authorTextStyle: TextStyle? = null,
        authorMaxLines: Int = nameMaxLines,
        descriptionTextStyle: TextStyle? = null,
        descriptionMaxLines: Int = 3,
        licensesTextStyle: TextStyle? = null,
    ): LibraryTextStyles = DefaultLibraryTextStyles(
        defaultOverflow = defaultOverflow,
        nameTextStyles = nameTextStyles,
        nameMaxLines = nameMaxLines,
        nameOverflow = nameOverflow,
        versionTextStyle = versionTextStyle,
        versionMaxLines = versionMaxLines,
        authorTextStyle = authorTextStyle,
        authorMaxLines = authorMaxLines,
        descriptionTextStyle = descriptionTextStyle,
        descriptionMaxLines = descriptionMaxLines,
        licensesTextStyle = licensesTextStyle,
    )
}

/**
 * Represents the background and content colors used in a library.
 */
@Stable
interface LibraryColors {
    /** Represents the background color for this library item. */
    val backgroundColor: Color

    /** Represents the content color for this library item. */
    val contentColor: Color

    /** Represents the badge background color for this library item. */
    val badgeBackgroundColor: Color

    /** Represents the badge content color for this library item. */
    val badgeContentColor: Color

    /** Represents the text color of the dialog's confirm button  */
    val dialogConfirmButtonColor: Color
}

/**
 * Default [LibraryColors].
 */
@Immutable
class DefaultLibraryColors(
    override val backgroundColor: Color,
    override val contentColor: Color,
    override val badgeBackgroundColor: Color,
    override val badgeContentColor: Color,
    override val dialogConfirmButtonColor: Color,
) : LibraryColors


/**
 * Represents the padding values used in a library.
 */
@Stable
interface LibraryPadding {
    /** Represents the padding inside the [Library] ui element */
    val contentPadding: PaddingValues

    /** Represents the padding around the name shown as part of a [Library] */
    val namePadding: PaddingValues

    /** Represents the padding around the version shown as part of a [Library] */
    val versionPadding: PaddingValues

    /** Represents the padding around a badge element shown as part of a [Library] */
    val badgePadding: PaddingValues

    /** Represents the padding around the content of a badge element shown as part of a [Library] */
    val badgeContentPadding: PaddingValues

    /** Represents the vertical padding between the individual items in the library element */
    val verticalPadding: Dp
}

/**
 * Default [LibraryPadding].
 */
@Immutable
private class DefaultLibraryPadding(
    override val contentPadding: PaddingValues,
    override val namePadding: PaddingValues,
    override val versionPadding: PaddingValues,
    override val badgePadding: PaddingValues,
    override val badgeContentPadding: PaddingValues,
    override val verticalPadding: Dp,
) : LibraryPadding


/**
 * Represents the padding values used in a library.
 */
@Stable
interface LibraryDimensions {
    /** Represents the spacing between items in the [Library] */
    val itemSpacing: Dp
}

/**
 * Default [LibraryDimensions].
 */
@Immutable
private class DefaultLibraryDimensions(
    override val itemSpacing: Dp,
) : LibraryDimensions

/**
 * Represents the text styles used in a library.
 */
@Stable
interface LibraryTextStyles {
    /** Represents the default text overflow for all line limited texts */
    val defaultOverflow: TextOverflow

    /** Represents the text styles for the name text */
    val nameTextStyles: TextStyle?

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

    /** Represents the text styles for the description text */
    val licensesTextStyle: TextStyle?
}

/**
 * Default [LibraryTextStyles].
 */
@Immutable
private class DefaultLibraryTextStyles(
    override val defaultOverflow: TextOverflow,
    override val nameTextStyles: TextStyle?,
    override val nameMaxLines: Int,
    override val nameOverflow: TextOverflow,
    override val versionTextStyle: TextStyle?,
    override val versionMaxLines: Int,
    override val authorTextStyle: TextStyle?,
    override val authorMaxLines: Int,
    override val descriptionTextStyle: TextStyle?,
    override val descriptionMaxLines: Int,
    override val licensesTextStyle: TextStyle?,
) : LibraryTextStyles