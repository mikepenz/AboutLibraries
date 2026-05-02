package com.mikepenz.aboutlibraries.ui.compose.variant.traditional

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.aboutlibraries.ui.compose.style.LibrariesStyle
import com.mikepenz.aboutlibraries.ui.compose.style.LibraryStrings
import com.mikepenz.aboutlibraries.ui.compose.style.orFallback
import com.mikepenz.aboutlibraries.ui.compose.variant.DefaultSearchField

/**
 * Theme-agnostic Traditional header — 48dp app icon + title/tagline + version chip + search.
 */
@Composable
fun TraditionalHeader(
    title: String,
    tagline: String?,
    versionLabel: String?,
    style: LibrariesStyle,
    strings: LibraryStrings,
    modifier: Modifier = Modifier,
    appIcon: (@Composable () -> Unit)? = null,
    showSearch: Boolean = true,
    searchQuery: String? = null,
    onSearchChange: ((String) -> Unit)? = null,
    search: (@Composable () -> Unit)? = null,
    onIconClick: (() -> Unit)? = null,
    onVersionClick: (() -> Unit)? = null,
) {
    val colors = style.colors
    val bg = colors.headerBackground.orFallback(Color.Transparent)
    val onBg = colors.headerOnBackground.orFallback(Color.Black)
    val subtle = colors.headerSubtleContent.orFallback(onBg.copy(alpha = 0.6f))
    val versionChipBg = colors.tabIdleBackground.orFallback(onBg.copy(alpha = 0.08f))
    val searchBg = colors.tabIdleBackground.orFallback(onBg.copy(alpha = 0.08f))

    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(bg)
                .padding(style.padding.headerPadding),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (appIcon != null) {
                    Box(
                        modifier = Modifier
                            .size(style.dimensions.headerIconSize)
                            .then(if (onIconClick != null) Modifier.clickable(onClick = onIconClick) else Modifier),
                        contentAlignment = Alignment.Center,
                    ) { appIcon() }
                    Spacer(Modifier.width(12.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    BasicText(text = title, style = style.textStyles.headerTitleTextStyle.copy(color = onBg), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (!tagline.isNullOrBlank()) {
                        BasicText(text = tagline, style = style.textStyles.headerTaglineTextStyle.copy(color = subtle), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                if (!versionLabel.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(versionChipBg)
                            .then(if (onVersionClick != null) Modifier.clickable(onClick = onVersionClick) else Modifier)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        BasicText(
                            text = versionLabel,
                            style = TextStyle(color = subtle, fontSize = 12.sp, fontWeight = FontWeight.Medium),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            if (showSearch) {
                Spacer(Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(style.dimensions.searchHeight)
                        .clip(style.shapes.headerSearchShape)
                        .background(searchBg)
                        .padding(horizontal = 16.dp),
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
        }
        // Bottom divider matching design's `borderBottom: 1px solid outlineVariant`
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.headerDivider.orFallback(onBg.copy(alpha = 0.2f))),
        )
    }
}
