package com.mikepenz.aboutlibraries.sample.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryActionMode
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryDetailMode
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrariesDensity
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrariesVariant

/**
 * The settings panel from the Sample App design.
 * On mobile this is a bottom sheet (handled by caller); on desktop/tablet it's a side drawer.
 *
 * This composable renders just the inner panel contents — the caller is responsible for
 * presenting it inside a [androidx.compose.material3.ModalBottomSheet] or a fixed-position
 * side panel.
 */
@Composable
fun SettingsPanel(
    settings: SampleSettings,
    onChange: (SampleSettings) -> Unit,
    onClose: () -> Unit,
    isMobile: Boolean,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .let { if (isMobile) it.fillMaxWidth().wrapContentHeight() else it.fillMaxHeight().width(320.dp) }
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .let {
                if (!isMobile) it.border(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant,
                    RoundedCornerShape(0.dp),
                ) else it
            },
    ) {
    val scrollModifier = if (constraints.hasBoundedHeight) Modifier.verticalScroll(rememberScrollState()) else Modifier
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(scrollModifier)
            .padding(
                start = if (isMobile) 18.dp else 20.dp,
                end = if (isMobile) 18.dp else 20.dp,
                top = if (isMobile) 14.dp else 18.dp,
                bottom = if (isMobile) 22.dp else 22.dp,
            ),
    ) {
        if (isMobile) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Settings",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.2).sp,
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        Section("Variant") {
            Segmented(
                options = listOf(
                    LibrariesVariant.Traditional to "Traditional",
                    LibrariesVariant.Refined to "Refined",
                ),
                selected = settings.variant,
                onSelect = { onChange(settings.copy(variant = it)) },
            )
        }

        Section("Header") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ToggleRow(
                    label = "Show library header",
                    on = settings.showHeader,
                    onChange = { onChange(settings.copy(showHeader = it)) },
                )
                Box(modifier = Modifier.alpha(if (settings.showHeader) 1f else 0.4f)) {
                    Segmented(
                        options = listOf(
                            HeaderStyle.Full to "Full",
                            HeaderStyle.Compact to "Compact",
                        ),
                        selected = settings.headerStyle,
                        onSelect = {
                            if (settings.showHeader) onChange(settings.copy(headerStyle = it))
                        },
                    )
                }
                Box(modifier = Modifier.alpha(if (settings.showHeader) 1f else 0.4f)) {
                    ToggleRow(
                        label = "Search bar",
                        on = settings.showSearch,
                        onChange = {
                            if (settings.showHeader) onChange(settings.copy(showSearch = it))
                        },
                    )
                }
                ToggleRow(
                    label = "License filter bar",
                    on = settings.showLicenseFilter,
                    onChange = { onChange(settings.copy(showLicenseFilter = it)) },
                )
            }
        }

        Section("Density") {
            Segmented(
                options = listOf(
                    LibrariesDensity.Cozy to "Cozy",
                    LibrariesDensity.Compact to "Compact",
                ),
                selected = settings.density,
                onSelect = { onChange(settings.copy(density = it)) },
            )
        }

        Section("Detail mode") {
            Segmented(
                options = listOf(
                    LibraryDetailMode.None to "None",
                    LibraryDetailMode.Inline to "Inline",
                    LibraryDetailMode.Sheet to "Sheet",
                ),
                selected = settings.detailMode,
                onSelect = { onChange(settings.copy(detailMode = it)) },
            )
        }

        Section("Action mode") {
            Segmented(
                options = listOf(
                    LibraryActionMode.Chips to "Chips",
                    LibraryActionMode.Icons to "Icons",
                    LibraryActionMode.Links to "Links",
                ),
                selected = settings.actionMode,
                onSelect = { onChange(settings.copy(actionMode = it)) },
            )
        }

        Section("Accent") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AccentSwatches.forEach { color ->
                    val selected = color == settings.accent
                    Box(
                        modifier = Modifier.size(36.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (selected) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        shape = RoundedCornerShape(18.dp),
                                    ),
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(color)
                                .clickable { onChange(settings.copy(accent = color)) },
                        )
                    }
                }
            }
        }

        Section("Theme") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Segmented(
                    options = listOf(false to "Light", true to "Dark"),
                    selected = settings.darkTheme,
                    onSelect = { onChange(settings.copy(darkTheme = it)) },
                )
                ToggleRow(
                    label = "High contrast",
                    on = settings.highContrast,
                    onChange = { onChange(settings.copy(highContrast = it)) },
                )
            }
        }

        Section("Show fields") {
            Column {
                ToggleRow("Author", settings.showAuthor) { onChange(settings.copy(showAuthor = it)) }
                ToggleRow("Version", settings.showVersion) { onChange(settings.copy(showVersion = it)) }
                ToggleRow("Description", settings.showDescription) { onChange(settings.copy(showDescription = it)) }
                ToggleRow("License", settings.showLicense) { onChange(settings.copy(showLicense = it)) }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant),
        )
        Text(
            text = "Settings are saved locally. This sample app demonstrates every public configuration of the AboutLibraries component — variant, density, detail surface, and action style.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 14.dp),
        )
    }
    }
}

@Composable
private fun Section(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = label.uppercase(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        content()
    }
}

@Composable
private fun ToggleRow(
    label: String,
    on: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.5.sp,
            modifier = Modifier.weight(1f),
        )
        PillToggle(on = on, onToggle = { onChange(!on) }, contentDescription = label)
    }
}

