package com.mikepenz.aboutlibraries.ui.compose.variant.refined

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.style.LibrariesStyle
import com.mikepenz.aboutlibraries.ui.compose.style.LibraryStrings
import com.mikepenz.aboutlibraries.ui.compose.style.orFallback
import com.mikepenz.aboutlibraries.ui.compose.variant.DefaultSearchField

/** A single license tab descriptor. `spdxId == null` represents the catch-all "All" tab. */
@Immutable
data class LicenseTab(val spdxId: String?, val label: String, val count: Int)

private const val ALL_TAB_KEY = "__all__"

/**
 * Theme-agnostic Refined header — title row, optional search, optional license tabs, bottom divider.
 *
 * @param inlineSearch When true the search field appears to the right of the title on the same row
 *   (compact header style). When false it appears below the title row (default).
 */
@Composable
fun RefinedHeader(
    title: String,
    subtitle: String?,
    style: LibrariesStyle,
    strings: LibraryStrings,
    modifier: Modifier = Modifier,
    appIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    showSearch: Boolean = true,
    searchQuery: String? = null,
    onSearchChange: ((String) -> Unit)? = null,
    search: (@Composable () -> Unit)? = null,
    tabs: List<LicenseTab> = emptyList(),
    selectedTab: String? = null,
    onTabSelected: ((String?) -> Unit)? = null,
    inlineSearch: Boolean = false,
) {
    val colors = style.colors
    val bg = colors.headerBackground.orFallback(Color.Transparent)
    val onBg = colors.headerOnBackground.orFallback(Color.Black)
    val subtle = colors.headerSubtleContent.orFallback(onBg.copy(alpha = 0.6f))
    val divider = colors.headerDivider.orFallback(onBg.copy(alpha = 0.2f))
    val searchBg = colors.tabIdleBackground.orFallback(onBg.copy(alpha = 0.08f))

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(bg),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(style.padding.headerPadding)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (appIcon != null) {
                    Box(
                        modifier = Modifier.size(style.dimensions.headerIconSize),
                        contentAlignment = Alignment.Center,
                    ) { appIcon() }
                    Spacer(Modifier.width(10.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    BasicText(text = title, style = style.textStyles.headerTitleTextStyle.copy(color = onBg), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (!subtitle.isNullOrBlank()) {
                        BasicText(text = subtitle, style = style.textStyles.headerTaglineTextStyle.copy(color = subtle), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                // Inline search: placed to the right of the title on the same row (compact style).
                if (showSearch && inlineSearch) {
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(style.dimensions.searchHeight)
                            .clip(style.shapes.headerSearchShape)
                            .background(searchBg)
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (search != null) {
                            search()
                        } else if (onSearchChange != null) {
                            DefaultSearchField(
                                query = searchQuery.orEmpty(),
                                onQueryChange = onSearchChange,
                                placeholder = strings.searchPlaceholder,
                                contentColor = onBg,
                                placeholderColor = subtle,
                                textStyle = style.textStyles.headerTaglineTextStyle,
                            )
                        }
                    }
                }
                if (actions != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) { actions() }
                }
            }

            // Below-title search: default layout (not inline).
            if (showSearch && !inlineSearch) {
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(style.dimensions.searchHeight)
                        .clip(style.shapes.headerSearchShape)
                        .background(searchBg)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (search != null) {
                        search()
                    } else if (onSearchChange != null) {
                        DefaultSearchField(
                            query = searchQuery.orEmpty(),
                            onQueryChange = onSearchChange,
                            placeholder = strings.searchPlaceholder,
                            contentColor = onBg,
                            placeholderColor = subtle,
                            textStyle = style.textStyles.headerTaglineTextStyle,
                        )
                    }
                }
            }

            if (tabs.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(tabs, key = { it.spdxId ?: ALL_TAB_KEY }, contentType = { "tab" }) { tab ->
                        LicenseTabItem(
                            tab = tab,
                            active = tab.spdxId == selectedTab,
                            style = style,
                            onBg = onBg,
                            subtle = subtle,
                            onTabSelected = onTabSelected,
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(divider),
        )
    }
}

@Composable
private fun LicenseTabItem(
    tab: LicenseTab,
    active: Boolean,
    style: LibrariesStyle,
    onBg: Color,
    subtle: Color,
    onTabSelected: ((String?) -> Unit)?,
) {
    val colors = style.colors
    val tabBg = if (active) colors.tabActiveBackground.orFallback(onBg.copy(alpha = 0.2f))
    else colors.tabIdleBackground.orFallback(onBg.copy(alpha = 0.08f))
    val tabFg = if (active) colors.tabActiveContent.orFallback(onBg)
    else colors.tabIdleContent.orFallback(subtle)
    val borderColor = if (active) colors.tabActiveBorder.orFallback(onBg.copy(alpha = 0.4f)) else Color.Transparent
    val dotColor = remember(tab.spdxId, colors.licenseHueResolver) {
        tab.spdxId?.let { colors.licenseHueResolver.colorFor(it) }
    }
    val currentOnTabSelected = rememberUpdatedState(onTabSelected)
    val onClick = remember(tab.spdxId) { { currentOnTabSelected.value?.invoke(tab.spdxId) ?: Unit } }

    Row(
        modifier = Modifier
            .clip(style.shapes.tabShape)
            .background(tabBg)
            .border(width = if (active) 1.dp else 0.dp, color = borderColor, shape = style.shapes.tabShape)
            .clickable(enabled = onTabSelected != null, onClick = onClick)
            .semantics {
                role = Role.Tab
                selected = active
            }
            .padding(style.padding.tabPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (dotColor != null) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor),
            )
        }
        BasicText(text = tab.label, style = style.textStyles.tabTextStyle.copy(color = tabFg))
        BasicText(
            text = tab.count.toString(),
            style = style.textStyles.tabCountTextStyle.copy(color = tabFg.copy(alpha = 0.7f)),
        )
    }
}
