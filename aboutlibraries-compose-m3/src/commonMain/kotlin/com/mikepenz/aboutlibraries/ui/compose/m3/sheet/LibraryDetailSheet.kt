package com.mikepenz.aboutlibraries.ui.compose.m3.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.style.DefaultLibraryActionBadges
import com.mikepenz.aboutlibraries.ui.compose.style.LibrariesStyle
import com.mikepenz.aboutlibraries.ui.compose.style.LibraryActionBadges
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryActionKind
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryActionMode
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrarySheetDetail

/**
 * Material 3 wrapper around the theme-agnostic [LibrarySheetDetail].
 *
 * Provides the Material `ModalBottomSheet` shell, drag handle, and surface colors. All inner
 * layout (title, meta, description, license body, action affordances) lives in core.
 *
 * Insets are split between three owners so each side is handled exactly once:
 *  - [DefaultDragHandle] owns the **top** (status-bar + display-cutout top) so the handle clears
 *    the camera notch when the sheet is fully expanded.
 *  - The sheet's `modifier` owns the **horizontal** sides via `windowInsetsPadding` (systemBars +
 *    displayCutout horizontal). Applying insets here constrains the sheet surface width itself
 *    rather than padding content inside, so text fills the available width naturally and the sheet
 *    does not extend behind side navigation bars or side cutouts. Because `windowInsetsPadding`
 *    marks insets as consumed, [LibrarySheetDetail] inside sees the horizontal values as zero and
 *    never double-pads.
 *  - [LibrarySheetDetail] owns the **bottom** (navigation bar + bottom display-cutout) for
 *    edge-to-edge scrolling.
 *
 * All three use `windowInsetsPadding`, whose consumption propagation means [contentWindowInsets]
 * overrides are automatically deduplicated — inner composables never double-pad.
 *
 * The top corners animate from the style's `sheetShape` rounding to square as the sheet is dragged
 * to the top edge (fully expanded, covering the screen), and back when collapsed. A sheet shorter
 * than the screen never reaches the top edge, so it keeps its rounded corners.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryDetailSheet(
    library: Library,
    onDismiss: () -> Unit,
    style: LibrariesStyle,
    modifier: Modifier = Modifier,
    actionMode: LibraryActionMode = LibraryActionMode.Chips,
    actionLabels: LibraryActionBadges = DefaultLibraryActionBadges,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    contentWindowInsets: @Composable () -> WindowInsets = { WindowInsets(0, 0, 0, 0) },
    dragHandle: (@Composable () -> Unit)? = { DefaultDragHandle(style) },
    onActionClick: ((Library, LibraryActionKind) -> Boolean)? = null,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier.windowInsetsPadding(
            WindowInsets.systemBars.union(WindowInsets.displayCutout)
                .only(WindowInsetsSides.Horizontal),
        ),
        sheetState = sheetState,
        shape = rememberExpandingSheetShape(style.shapes.sheetShape, sheetState),
        containerColor = style.colors.sheetSurface.takeOrElse { MaterialTheme.colorScheme.surfaceContainerHigh },
        contentColor = style.colors.rowOnBackground.takeOrElse { MaterialTheme.colorScheme.onSurface },
        scrimColor = style.colors.sheetScrim.takeOrElse { MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f) },
        contentWindowInsets = contentWindowInsets,
        dragHandle = dragHandle,
    ) {
        LibrarySheetDetail(
            library = library,
            actionMode = actionMode,
            style = style,
            actionLabels = actionLabels,
            onActionClick = onActionClick,
        )
    }
}

@Composable
private fun DefaultDragHandle(style: LibrariesStyle) {
    Box(
        modifier = Modifier
            // Push the handle below whatever sits at the top edge (status bar / display cutout, or
            // the nav bar in reverse-portrait) when the sheet is fully expanded. `windowInsetsPadding`
            // respects the offset-based consumption ModalBottomSheet applies, so this resolves to zero
            // while the sheet is only partially expanded (not at the top edge).
            .windowInsetsPadding(WindowInsets.systemBars.union(WindowInsets.displayCutout).only(WindowInsetsSides.Top))
            .padding(top = 12.dp, bottom = 16.dp)
            .size(width = style.dimensions.dragHandleWidth, height = style.dimensions.dragHandleHeight)
            .clip(RoundedCornerShape(style.dimensions.dragHandleHeight / 2))
            .background(style.colors.sheetDragHandle.takeOrElse { MaterialTheme.colorScheme.outline.copy(alpha = 0.5f) }),
    )
}

/** Travel over which the top corners square off as the sheet's top edge nears the container top. */
private val SheetCornerSquareDistance = 32.dp

/**
 * Returns [base] with its top corners squared in proportion to how close the sheet is to the top edge.
 *
 * The rounding is a pure function of the sheet's position ([SheetState.requireOffset] — distance from
 * the top of its container): fully rounded while the top edge is more than [SheetCornerSquareDistance]
 * away, linearly squaring to a flat edge as it meets the top (fully expanded, covering the screen).
 * Because it tracks position rather than animating off a settled state, the corners square *during*
 * the open/drag motion and are already flat on arrival — no post-expansion flash of rounded corners.
 *
 * A sheet shorter than the screen settles with a large offset and so keeps its rounded corners.
 * Returns [base] verbatim when it is not a [RoundedCornerShape] (nothing to interpolate) or while
 * fully rounded (stable identity, no needless re-clipping).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun rememberExpandingSheetShape(base: Shape, sheetState: SheetState): Shape {
    if (base !is RoundedCornerShape) return base
    val distancePx = with(LocalDensity.current) { SheetCornerSquareDistance.toPx() }
    val fraction by remember(sheetState, distancePx) {
        derivedStateOf {
            val offset = runCatching { sheetState.requireOffset() }.getOrDefault(Float.MAX_VALUE)
            (offset / distancePx).coerceIn(0f, 1f)
        }
    }
    return if (fraction >= 1f) {
        base
    } else {
        RoundedCornerShape(
            topStart = ScaledCornerSize(base.topStart, fraction),
            topEnd = ScaledCornerSize(base.topEnd, fraction),
            bottomEnd = base.bottomEnd,
            bottomStart = base.bottomStart,
        )
    }
}

/** A [CornerSize] that scales [base] by [fraction] (0 = square, 1 = original). */
private data class ScaledCornerSize(private val base: CornerSize, private val fraction: Float) : CornerSize {
    override fun toPx(shapeSize: Size, density: Density): Float = base.toPx(shapeSize, density) * fraction
}
