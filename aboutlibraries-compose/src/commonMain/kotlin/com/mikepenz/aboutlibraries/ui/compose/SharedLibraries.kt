package com.mikepenz.aboutlibraries.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Badge
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.util.author

@Composable
internal fun Library(
    library: Library,
    showAuthor: Boolean = true,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true,
    colors: LibraryColors = LibraryDefaults.libraryColors(),
    contentPadding: PaddingValues = LibraryDefaults.ContentPadding,
    onClick: () -> Unit,
) {
    val typography = MaterialTheme.typography
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.backgroundColor)
            .clickable { onClick.invoke() }
            .padding(contentPadding)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = library.name,
                modifier = Modifier
                    .padding(top = LibraryDefaults.LibraryNamePaddingTop)
                    .weight(1f),
                style = typography.h6,
                color = colors.contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val version = library.artifactVersion
            if (version != null && showVersion) {
                Text(
                    version,
                    modifier = Modifier.padding(start = LibraryDefaults.LibraryVersionPaddingStart),
                    style = typography.body2,
                    color = colors.contentColor,
                    textAlign = TextAlign.Center
                )
            }
        }
        val author = library.author
        if (showAuthor && author.isNotBlank()) {
            Text(
                text = author,
                style = typography.body2,
                color = colors.contentColor
            )
        }
        if (showLicenseBadges && library.licenses.isNotEmpty()) {
            Row(modifier = Modifier.padding(top = LibraryDefaults.LibraryBadgePaddingTop)) {
                library.licenses.forEach {
                    Badge(
                        modifier = Modifier.padding(end = LibraryDefaults.LibraryBadgePaddingEnd),
                        contentColor = colors.badgeContentColor,
                        backgroundColor = colors.badgeBackgroundColor
                    ) {
                        Text(text = it.name)
                    }
                }
            }
        }
    }
}


/**
 * Contains the default values used by [Library]
 */
object LibraryDefaults {
    private val LibraryItemPadding = 16.dp
    internal val LibraryNamePaddingTop = 4.dp
    internal val LibraryVersionPaddingStart = 8.dp
    internal val LibraryBadgePaddingTop = 8.dp
    internal val LibraryBadgePaddingEnd = 4.dp

    /**
     * The default content padding used by [Library]
     */
    val ContentPadding = PaddingValues(LibraryItemPadding)

    /**
     * Creates a [LibraryColors] that represents the default colors used in
     * a [Library].
     *
     * @param backgroundColor the background color of this [Library]
     * @param contentColor the content color of this [Library]
     * @param badgeBackgroundColor the badge background color of this [Library]
     * @param badgeContentColor the badge content color of this [Library]
     */
    @Composable
    fun libraryColors(
        backgroundColor: Color = MaterialTheme.colors.background,
        contentColor: Color = contentColorFor(backgroundColor),
        badgeBackgroundColor: Color = MaterialTheme.colors.primary,
        badgeContentColor: Color = contentColorFor(badgeBackgroundColor),
    ): LibraryColors = DefaultLibraryColors(
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        badgeBackgroundColor = badgeBackgroundColor,
        badgeContentColor = badgeContentColor
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
}

/**
 * Default [LibraryColors].
 */
@Immutable
private class DefaultLibraryColors(
    override val backgroundColor: Color,
    override val contentColor: Color,
    override val badgeBackgroundColor: Color,
    override val badgeContentColor: Color,
) : LibraryColors