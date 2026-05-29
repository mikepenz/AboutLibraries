package com.mikepenz.aboutlibraries.ui.compose.variant

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.style.LibrariesStyle
import com.mikepenz.aboutlibraries.ui.compose.style.orFallback
import com.mikepenz.aboutlibraries.ui.compose.variant.refined.RefinedRow
import com.mikepenz.aboutlibraries.ui.compose.variant.traditional.TraditionalRow

/**
 * The default item renderer used by [Libraries] / `LibrariesContainer`.
 *
 * Owns the full per-item visual: the lazy [Modifier.animateItem] wrapper, the optional
 * [expandedBackground], the [variant] row dispatch, and the animated [inlineDetail] expansion.
 *
 * Exposed publicly so integrators can reuse the stock rendering inside their own `libraryRow`
 * slot (or their own `LazyColumn`) while adjusting [modifier] — e.g.
 * `LibraryRow(library, …, modifier = Modifier.padding(horizontal = 8.dp))`.
 *
 * Declared on [LazyItemScope] because it calls [Modifier.animateItem]; invoke it from within a
 * lazy list item.
 */
@Composable
fun LazyItemScope.LibraryRow(
    library: Library,
    expanded: Boolean,
    onToggle: () -> Unit,
    style: LibrariesStyle,
    modifier: Modifier = Modifier,
    variant: LibrariesVariant = LibrariesVariant.Traditional,
    density: LibrariesDensity = LibrariesDensity.Cozy,
    badges: LibraryBadges = DefaultLibraryBadges,
    expandedBackground: Color = style.colors.rowExpandedBackground.orFallback(Color.Unspecified),
    inlineDetail: (@Composable (Library) -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .animateItem()
            .then(if (expanded && expandedBackground != Color.Unspecified) Modifier.background(expandedBackground) else Modifier),
    ) {
        when (variant) {
            LibrariesVariant.Traditional -> TraditionalRow(
                library = library,
                expanded = expanded,
                onToggle = onToggle,
                density = density,
                badges = badges,
                style = style,
            )

            LibrariesVariant.Refined -> RefinedRow(
                library = library,
                expanded = expanded,
                onToggle = onToggle,
                density = density,
                badges = badges,
                style = style,
            )
        }
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
}
