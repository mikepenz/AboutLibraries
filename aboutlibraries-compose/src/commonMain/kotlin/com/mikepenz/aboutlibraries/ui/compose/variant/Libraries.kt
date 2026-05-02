package com.mikepenz.aboutlibraries.ui.compose.variant

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.style.DefaultLibraryActionLabels
import com.mikepenz.aboutlibraries.ui.compose.style.LibrariesStyle
import com.mikepenz.aboutlibraries.ui.compose.style.LibraryActionLabels
import com.mikepenz.aboutlibraries.ui.compose.style.orFallback
import com.mikepenz.aboutlibraries.ui.compose.variant.refined.RefinedRow
import com.mikepenz.aboutlibraries.ui.compose.variant.traditional.TraditionalRow

/**
 * Theme-agnostic libraries list entry point.
 *
 * Dispatches to the chosen [variant]'s row composable. All [LibraryDetailMode] and
 * [LibraryActionMode] combinations are valid for both variants — the inner scaffold handles
 * inline expansion and the calling adapter handles sheet presentation via [onSheetRequest].
 */
@Composable
fun Libraries(
    libraries: List<Library>,
    style: LibrariesStyle,
    modifier: Modifier = Modifier,
    variant: LibrariesVariant = LibrariesVariant.Traditional,
    density: LibrariesDensity = LibrariesDensity.Cozy,
    detailMode: LibraryDetailMode = LibraryDetailMode.Inline,
    actionMode: LibraryActionMode = LibraryActionMode.Chips,
    badges: LibraryBadges = DefaultLibraryBadges,
    actionLabels: LibraryActionLabels = DefaultLibraryActionLabels,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState(),
    itemSpacing: Dp = 0.dp,
    header: (LazyListScope.() -> Unit)? = null,
    divider: (@Composable LazyItemScope.() -> Unit)? = null,
    footer: (LazyListScope.() -> Unit)? = null,
    onLibraryClick: ((Library) -> Boolean)? = null,
    onSheetRequest: ((Library) -> Unit)? = null,
    onActionClick: ((Library, LibraryActionKind) -> Boolean)? = null,
    onDialogRequest: ((Library) -> Unit)? = null,
) {
    val row: @Composable LazyItemScope.(Library, Boolean, () -> Unit) -> Unit =
        remember(variant, density, badges, style) {
            { library, expanded, toggle ->
                when (variant) {
                    LibrariesVariant.Traditional -> TraditionalRow(
                        library = library,
                        expanded = expanded,
                        onToggle = toggle,
                        density = density,
                        badges = badges,
                        style = style,
                    )

                    LibrariesVariant.Refined -> RefinedRow(
                        library = library,
                        expanded = expanded,
                        onToggle = toggle,
                        density = density,
                        badges = badges,
                        style = style,
                    )
                }
            }
        }

    val inlineDetail: (@Composable (Library) -> Unit)? = remember(
        detailMode, variant, style, actionMode, actionLabels, onActionClick, onDialogRequest,
    ) {
        if (detailMode != LibraryDetailMode.Inline) null else { library: Library ->
            when (variant) {
                // Traditional row already renders description and license chips; the inline
                // expansion only adds the action affordances below.
                LibrariesVariant.Traditional -> Box(Modifier.padding(style.padding.inlineActionsPadding)) {
                    LibraryActions(
                        library = library,
                        actionMode = actionMode,
                        style = style,
                        actionLabels = actionLabels,
                        onActionClick = onActionClick,
                        onLicenseContentRequest = onDialogRequest,
                    )
                }
                // Refined row only shows name/version/author/license-label — the inline body
                // adds description and actions, indented under the row content.
                LibrariesVariant.Refined -> LibraryInlineDetail(
                    library = library,
                    actionMode = actionMode,
                    style = style,
                    actionLabels = actionLabels,
                    onActionClick = onActionClick,
                    onDialogRequest = onDialogRequest,
                )
            }
        }
    }

    LibraryListScaffold(
        libraries = libraries,
        row = row,
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        itemSpacing = itemSpacing,
        detailMode = detailMode,
        onLibraryClick = onLibraryClick,
        onSheetRequest = onSheetRequest,
        header = header,
        divider = divider,
        footer = footer,
        expandedBackground = style.colors.rowExpandedBackground.orFallback(Color.Unspecified),
        inlineDetail = inlineDetail,
    )
}
