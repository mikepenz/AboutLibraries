package com.mikepenz.aboutlibraries.ui.compose.variant

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library

/**
 * Unified `LazyColumn` driver shared by Traditional and Refined variants.
 *
 * Stateful overload — owns per-row inline-expand state via [rememberSaveable] (survives rotation
 * and process death). For programmatic control or testing, use the stateless overload below.
 */
@Composable
fun LibraryListScaffold(
    libraries: List<Library>,
    row: @Composable LazyItemScope.(library: Library, expanded: Boolean, toggle: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    itemSpacing: Dp = 0.dp,
    detailMode: LibraryDetailMode = LibraryDetailMode.None,
    onLibraryClick: ((Library) -> Boolean)? = null,
    onSheetRequest: ((Library) -> Unit)? = null,
    inlineDetail: (@Composable (Library) -> Unit)? = null,
    expandedBackground: Color = Color.Unspecified,
    header: (LazyListScope.() -> Unit)? = null,
    divider: (@Composable LazyItemScope.() -> Unit)? = null,
    footer: (LazyListScope.() -> Unit)? = null,
) {
    var expandedId by rememberSaveable { mutableStateOf<String?>(null) }
    LibraryListScaffold(
        libraries = libraries,
        expandedLibraryId = expandedId,
        onExpandedLibraryIdChange = { expandedId = it },
        row = row,
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        itemSpacing = itemSpacing,
        detailMode = detailMode,
        onLibraryClick = onLibraryClick,
        onSheetRequest = onSheetRequest,
        inlineDetail = inlineDetail,
        expandedBackground = expandedBackground,
        header = header,
        divider = divider,
        footer = footer,
    )
}

/**
 * Stateless variant — caller owns the [expandedLibraryId] state. Use this overload to drive
 * expansion programmatically or to mirror state across multiple components.
 */
@Composable
fun LibraryListScaffold(
    libraries: List<Library>,
    expandedLibraryId: String?,
    onExpandedLibraryIdChange: (String?) -> Unit,
    row: @Composable LazyItemScope.(library: Library, expanded: Boolean, toggle: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    itemSpacing: Dp = 0.dp,
    detailMode: LibraryDetailMode = LibraryDetailMode.None,
    onLibraryClick: ((Library) -> Boolean)? = null,
    onSheetRequest: ((Library) -> Unit)? = null,
    inlineDetail: (@Composable (Library) -> Unit)? = null,
    expandedBackground: Color = Color.Unspecified,
    header: (LazyListScope.() -> Unit)? = null,
    divider: (@Composable LazyItemScope.() -> Unit)? = null,
    footer: (LazyListScope.() -> Unit)? = null,
) {
    val verticalArrangement = remember(itemSpacing) {
        if (itemSpacing.value > 0f) Arrangement.spacedBy(itemSpacing) else Arrangement.Top
    }
    LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
    ) {
        header?.invoke(this)
        itemsIndexed(
            items = libraries,
            key = { _, l -> l.uniqueId },
            contentType = { _, _ -> LibraryItemContentType },
        ) { index, library ->
            // Per-row toggle is memoized on the row's identity so [TraditionalRow]/[RefinedRow]
            // see a stable lambda reference and can skip when [expanded] is unchanged.
            // expandedLibraryId must be a key so the lambda captures its current value;
            // without it the closure would stale-capture the value at first composition
            // and a second click would never collapse the row.
            val toggle = remember(library.uniqueId, detailMode, onLibraryClick, onSheetRequest, expandedLibraryId) {
                {
                    val handled = onLibraryClick?.invoke(library) ?: false
                    if (!handled) when (detailMode) {
                        LibraryDetailMode.None -> {}
                        LibraryDetailMode.Inline -> {
                            onExpandedLibraryIdChange(if (expandedLibraryId == library.uniqueId) null else library.uniqueId)
                        }

                        LibraryDetailMode.Sheet -> onSheetRequest?.invoke(library)
                    }
                }
            }
            val expanded = detailMode == LibraryDetailMode.Inline && expandedLibraryId == library.uniqueId

            val wrapperModifier = if (expanded && expandedBackground != Color.Unspecified) {
                Modifier.animateItem().background(expandedBackground)
            } else Modifier.animateItem()
            Column(modifier = wrapperModifier) {
                row(library, expanded, toggle)
                AnimatedVisibility(
                    visible = expanded && inlineDetail != null,
                    enter = expandVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow,
                        ),
                    ),
                    exit = shrinkVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow,
                        ),
                    ),
                ) {
                    inlineDetail?.invoke(library)
                }
            }

            if (divider != null && index < libraries.lastIndex) {
                divider.invoke(this)
            }
        }
        footer?.invoke(this)
    }
}

private const val LibraryItemContentType = "library"
