package com.mikepenz.aboutlibraries.ui.compose.variant

import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.style.DefaultLibraryActionBadges
import com.mikepenz.aboutlibraries.ui.compose.style.LibrariesStyle
import com.mikepenz.aboutlibraries.ui.compose.style.LibraryActionBadges
import com.mikepenz.aboutlibraries.ui.compose.style.orFallback

/**
 * Theme-agnostic libraries list entry point.
 *
 * Dispatches to the chosen [variant]'s row composable. All [LibraryDetailMode] and
 * [LibraryActionMode] combinations are valid for both variants — the default [libraryRow] owns
 * inline expansion and the calling adapter handles sheet presentation via [onSheetRequest].
 *
 * Override [libraryRow] to take full control of an item's rendering. The slot owns the entire
 * item visual — supply your own `Modifier.animateItem()`, expanded background, and (if desired)
 * inline-detail expansion; the default delegates to the variant rows and animates the detail.
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
    actionLabels: LibraryActionBadges = DefaultLibraryActionBadges,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState(),
    itemSpacing: Dp = 0.dp,
    header: (LazyListScope.() -> Unit)? = null,
    divider: (@Composable LazyItemScope.() -> Unit)? = null,
    footer: (LazyListScope.() -> Unit)? = null,
    libraryRow: (@Composable LazyItemScope.(index: Int, library: Library, expanded: Boolean, toggle: () -> Unit, style: LibrariesStyle) -> Unit)? = null,
    onLibraryClick: ((Library) -> Boolean)? = null,
    onSheetRequest: ((Library) -> Unit)? = null,
    onActionClick: ((Library, LibraryActionKind) -> Boolean)? = null,
    onDialogRequest: ((Library) -> Unit)? = null,
    overscrollEffect: OverscrollEffect? = rememberOverscrollEffect(),
) {
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

    val expandedBackground = style.colors.rowExpandedBackground.orFallback(Color.Unspecified)

    // Adapts the public [libraryRow] slot (which receives the resolved [style]) to the scaffold's
    // style-agnostic row contract. When no override is supplied it delegates to the public
    // [LibraryRow], which owns the per-item wrapper (item animation + expanded background), the
    // variant dispatch, and the inline-detail expansion.
    val resolvedRow: @Composable LazyItemScope.(index: Int, library: Library, expanded: Boolean, toggle: () -> Unit) -> Unit =
        { index, library, expanded, toggle ->
            val rowContent = libraryRow
            if (rowContent != null) {
                rowContent(index, library, expanded, toggle, style)
            } else {
                LibraryRow(
                    library = library,
                    expanded = expanded,
                    onToggle = toggle,
                    style = style,
                    variant = variant,
                    density = density,
                    badges = badges,
                    expandedBackground = expandedBackground,
                    inlineDetail = inlineDetail,
                )
            }
        }

    LibraryListScaffold(
        libraries = libraries,
        row = resolvedRow,
        modifier = modifier,
        state = state,
        overscrollEffect = overscrollEffect,
        contentPadding = contentPadding,
        itemSpacing = itemSpacing,
        detailMode = detailMode,
        onLibraryClick = onLibraryClick,
        onSheetRequest = onSheetRequest,
        header = header,
        divider = divider,
        footer = footer,
    )
}