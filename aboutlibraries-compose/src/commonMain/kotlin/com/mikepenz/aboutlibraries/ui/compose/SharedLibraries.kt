package com.mikepenz.aboutlibraries.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Badge
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.util.author

/**
 * Displays all provided libraries in a simple list.
 */
@Composable
fun Libraries(
    libraries: List<Library>,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showAuthor: Boolean = true,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true,
    colors: LibraryColors = LibraryDefaults.libraryColors(),
    padding: LibraryPadding = LibraryDefaults.libraryPadding(),
    itemContentPadding: PaddingValues = LibraryDefaults.ContentPadding,
    itemSpacing: Dp = LibraryDefaults.LibraryItemSpacing,
    header: (LazyListScope.() -> Unit)? = null,
    onLibraryClick: ((Library) -> Unit)? = null,
) {
    val uriHandler = LocalUriHandler.current

    LazyColumn(
        modifier,
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        state = lazyListState,
        contentPadding = contentPadding
    ) {
        header?.invoke(this)
        libraryItems(
            libraries,
            showAuthor,
            showVersion,
            showLicenseBadges,
            colors,
            padding,
            itemContentPadding
        ) { library ->
            val license = library.licenses.firstOrNull()
            if (onLibraryClick != null) {
                onLibraryClick.invoke(library)
            } else if (!license?.url.isNullOrBlank()) {
                license?.url?.also { uriHandler.openUri(it) }
            }
        }
    }
}

internal inline fun LazyListScope.libraryItems(
    libraries: List<Library>,
    showAuthor: Boolean = true,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true,
    colors: LibraryColors,
    padding: LibraryPadding,
    itemContentPadding: PaddingValues = LibraryDefaults.ContentPadding,
    crossinline onLibraryClick: ((Library) -> Unit),
) {
    items(libraries) { library ->
        Library(
            library,
            showAuthor,
            showVersion,
            showLicenseBadges,
            colors,
            padding,
            itemContentPadding
        ) {
            onLibraryClick.invoke(library)
        }
    }
}

@Composable
internal fun Library(
    library: Library,
    showAuthor: Boolean = true,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true,
    colors: LibraryColors = LibraryDefaults.libraryColors(),
    padding: LibraryPadding = LibraryDefaults.libraryPadding(),
    contentPadding: PaddingValues = LibraryDefaults.ContentPadding,
    typography: Typography = MaterialTheme.typography,
    onClick: () -> Unit,
) {
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
                    .padding(padding.namePadding)
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
                    modifier = Modifier.padding(padding.versionPadding),
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
            Row {
                library.licenses.forEach {
                    Badge(
                        modifier = Modifier.padding(padding.badgePadding),
                        contentColor = colors.badgeContentColor,
                        backgroundColor = colors.badgeBackgroundColor
                    ) {
                        Text(
                            modifier = Modifier.padding(padding.badgeContentPadding),
                            text = it.name
                        )
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
    private val LibraryNamePaddingTop = 4.dp
    private val LibraryVersionPaddingStart = 8.dp
    private val LibraryBadgePaddingTop = 8.dp
    private val LibraryBadgePaddingEnd = 4.dp
    internal val LibraryItemSpacing = 0.dp

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

    /**
     * Creates a [LibraryPadding] that represents the default paddings used in a [Library]
     *
     * @param namePadding the padding around the name shown as part of a [Library]
     * @param versionPadding the padding around the version shown as part of a [Library]
     * @param badgePadding the padding around a badge element shown as part of a [Library]
     * @param badgeContentPadding the padding around the content of a badge element shown as part of a [Library]
     */
    @Composable
    fun libraryPadding(
        namePadding: PaddingValues = PaddingValues(top = LibraryNamePaddingTop),
        versionPadding: PaddingValues = PaddingValues(start = LibraryVersionPaddingStart),
        badgePadding: PaddingValues = PaddingValues(
            top = LibraryBadgePaddingTop,
            end = LibraryBadgePaddingEnd
        ),
        badgeContentPadding: PaddingValues = PaddingValues(0.dp),
    ): LibraryPadding = DefaultLibraryPadding(
        namePadding = namePadding,
        versionPadding = versionPadding,
        badgePadding = badgePadding,
        badgeContentPadding = badgeContentPadding,
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


/**
 * Represents the padding values used in a library.
 */
@Stable
interface LibraryPadding {
    /** Represents the padding around the name shown as part of a [Library] */
    val namePadding: PaddingValues

    /** Represents the padding around the version shown as part of a [Library] */
    val versionPadding: PaddingValues

    /** Represents the padding around a badge element shown as part of a [Library] */
    val badgePadding: PaddingValues

    /** Represents the padding around the content of a badge element shown as part of a [Library] */
    val badgeContentPadding: PaddingValues
}

/**
 * Default [LibraryPadding].
 */
@Immutable
private class DefaultLibraryPadding(
    override val namePadding: PaddingValues,
    override val versionPadding: PaddingValues,
    override val badgePadding: PaddingValues,
    override val badgeContentPadding: PaddingValues,
) : LibraryPadding
