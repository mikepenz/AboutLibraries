package com.mikepenz.aboutlibraries.ui.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library


/**
 * Contains the default values used by [Library]
 */
object LibraryDefaults {
    private val LibraryVersionPaddingStart = 8.dp
    private val LibraryBadgePaddingTop = 8.dp
    private val LibraryBadgePaddingEnd = 4.dp

    /** The default content padding used by [Library] */
    private val LibraryItemContentPadding = PaddingValues(16.dp)

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
        contentPadding: PaddingValues = LibraryItemContentPadding,
        namePadding: PaddingValues = PaddingValues(0.dp),
        versionPadding: PaddingValues = PaddingValues(start = LibraryVersionPaddingStart),
        badgePadding: PaddingValues = PaddingValues(
            top = LibraryBadgePaddingTop, end = LibraryBadgePaddingEnd
        ),
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

    private val LibraryItemSpacing = 0.dp

    /**
     * Creates a [LibraryDimensions] that represents the default dimensions used in a [Library]
     *
     * @param itemSpacing the spacing between items in the [Library]
     */
    fun libraryDimensions(
        itemSpacing: Dp = LibraryItemSpacing,
    ): LibraryDimensions = DefaultLibraryDimensions(
        itemSpacing = itemSpacing,
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