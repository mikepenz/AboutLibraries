package com.mikepenz.aboutlibraries.ui.compose.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.util.fastFirst
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.LibraryPadding

/**
 * A layout that displays a library in a scaffold-like manner.
 *
 * @param name the name of the library
 * @param version the version of the library
 * @param author the author of the library
 * @param description the description of the library
 * @param licenses the licenses of the library
 * @param modifier the modifier to apply to this layout
 * @param libraryPadding the padding to apply to the library
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LibraryScaffoldLayout(
    name: @Composable BoxScope.() -> Unit,
    version: @Composable BoxScope.() -> Unit,
    author: @Composable BoxScope.() -> Unit,
    description: @Composable BoxScope.() -> Unit,
    licenses: @Composable FlowRowScope.() -> Unit,
    actions: @Composable FlowRowScope.() -> Unit,
    modifier: Modifier = Modifier,
    libraryPadding: LibraryPadding = LibraryDefaults.libraryPadding(),
) {
    Layout(
        modifier = modifier.padding(libraryPadding.contentPadding),
        content = {
            Box(Modifier.layoutId(LibraryLayoutContent.Name).padding(libraryPadding.namePadding).fillMaxWidth(), content = name)
            Box(Modifier.layoutId(LibraryLayoutContent.Version), content = version)
            Box(Modifier.layoutId(LibraryLayoutContent.Author), content = author)
            Box(Modifier.layoutId(LibraryLayoutContent.Description), content = description)
            FlowRow(Modifier.layoutId(LibraryLayoutContent.Actions), content = {
                licenses()
                actions()
            })
        },
    ) { measurables, constraints ->
        // don't allow version to take more than 30%
        val versionMaxWidth = if (constraints.maxWidth == Constraints.Infinity) constraints.maxWidth else (constraints.maxWidth * 0.3f).toInt()
        val versionPlaceable = measurables.fastFirst { it.layoutId == LibraryLayoutContent.Version }.measure(constraints.copy(minWidth = 0, maxWidth = versionMaxWidth))

        val maxNameWidth = if (constraints.maxWidth == Constraints.Infinity) constraints.maxWidth else (constraints.maxWidth - versionPlaceable.width).coerceAtLeast(0)
        val namePlaceable = measurables.fastFirst { it.layoutId == LibraryLayoutContent.Name }.measure(constraints.copy(minWidth = 0, maxWidth = maxNameWidth))

        val nameYOffset = if (versionPlaceable.height > namePlaceable.height) (versionPlaceable.height - namePlaceable.height) / 2 else 0
        val versionYOffset = if (versionPlaceable.height < namePlaceable.height) (namePlaceable.height - versionPlaceable.height) / 2 else 0

        val topLineHeight = versionPlaceable.height.coerceAtLeast(namePlaceable.height) + libraryPadding.verticalPadding.toPx().toInt()
        val authorPlaceable = measurables.fastFirst { it.layoutId == LibraryLayoutContent.Author }.measure(constraints.copy(minWidth = 0))

        val authorGuideline = topLineHeight + authorPlaceable.height + libraryPadding.verticalPadding.toPx().toInt()
        val descriptionPlaceable = measurables.fastFirst { it.layoutId == LibraryLayoutContent.Description }.measure(constraints.copy(minWidth = 0))

        val descriptionGuideline = authorGuideline + descriptionPlaceable.height + libraryPadding.verticalPadding.toPx().toInt()
        val licensesPlaceable = measurables.fastFirst { it.layoutId == LibraryLayoutContent.Actions }.measure(constraints.copy(minWidth = 0))

        val layoutHeight = descriptionGuideline + licensesPlaceable.height

        layout(constraints.maxWidth, layoutHeight) {
            namePlaceable.placeRelative(x = 0, y = nameYOffset)
            versionPlaceable.placeRelative(x = namePlaceable.width, y = versionYOffset)
            authorPlaceable.placeRelative(x = 0, y = topLineHeight)
            descriptionPlaceable.placeRelative(x = 0, y = authorGuideline)
            licensesPlaceable.placeRelative(x = 0, y = descriptionGuideline)
        }
    }
}

private enum class LibraryLayoutContent {
    Name, Version, Author, Description, Actions
}