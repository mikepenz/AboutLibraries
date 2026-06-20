package com.mikepenz.aboutlibraries.sample.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.aboutlibraries.ui.compose.m3.style.accentDerivedLicenseHueResolver

/**
 * Horizontal scrollable license filter bar with hue dots, from `sample-app.jsx → LicenseFilterBar`.
 * `selectedSpdxId == null` represents the "All" tab.
 */
@Composable
fun LicenseFilterBar(
    tabs: List<LicenseFilterTab>,
    selectedSpdxId: String?,
    onSelect: (String?) -> Unit,
    isMobile: Boolean,
    modifier: Modifier = Modifier,
) {
    val licenseResolver = accentDerivedLicenseHueResolver()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        val activeBg = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
            .compositeOver(MaterialTheme.colorScheme.surfaceContainer)
        val activeBorderColor = MaterialTheme.colorScheme.primary
        val idleBg = MaterialTheme.colorScheme.surfaceContainer
        val idleBorderColor = Color.Transparent
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                horizontal = if (isMobile) 12.dp else 16.dp,
                vertical = if (isMobile) 8.dp else 10.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(tabs, key = { it.spdxId ?: "all" }, contentType = { "tab" }) { tab ->
                val active = tab.spdxId == selectedSpdxId
                val hue = tab.spdxId?.let { licenseResolver.colorFor(it) }
                val border = if (active) activeBorderColor else idleBorderColor

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(if (active) activeBg else idleBg)
                        .border(1.dp, border, RoundedCornerShape(100.dp))
                        .clickable { onSelect(tab.spdxId) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    if (hue != null) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(hue),
                        )
                    }
                    Text(
                        text = tab.label,
                        color = if (active) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = tab.count.toString(),
                        color = (if (active) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant).copy(alpha = 0.7f),
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant),
    )
}

data class LicenseFilterTab(val spdxId: String?, val label: String, val count: Int)
