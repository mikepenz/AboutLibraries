package com.mikepenz.aboutlibraries.sample.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.aboutlibraries.sample.icon.Github
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrariesVariant

/**
 * Top app bar from `sample-app.jsx → SampleHeader`. Renders app icon, title (+ subtitle on
 * desktop), three [PillToggle]s (theme, header, variant), GitHub link (desktop), and a
 * settings gear button.
 *
 * Heights: 56dp on mobile, 64dp on desktop. Bottom border `outlineVariant`.
 */
@Composable
fun SampleHeader(
    settings: SampleSettings,
    isMobile: Boolean,
    onToggleTheme: () -> Unit,
    onToggleHeader: () -> Unit,
    onToggleVariant: () -> Unit,
    onOpenGithub: (() -> Unit)?,
    onOpenSettings: () -> Unit,
    appName: String,
    appVersion: String,
    appIconLetter: String = "A",
) {
    val height = if (isMobile) 56.dp else 64.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = if (isMobile) 16.dp else 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (isMobile) 10.dp else 14.dp),
    ) {
        // App icon — primary squircle with single letter
        Box(
            modifier = Modifier
                .size(if (isMobile) 30.dp else 34.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = appIconLetter,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = if (isMobile) 15.sp else 17.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
            )
        }

        // Title + (desktop) subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isMobile) "AboutLibraries Sample" else "AboutLibraries · Compose Sample",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = if (isMobile) 15.sp else 17.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.2).sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!isMobile) {
                Text(
                    text = "v$appVersion · live preview",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.5.sp,
                    maxLines = 1,
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (isMobile) 4.dp else 6.dp),
        ) {
            PillToggle(
                on = !settings.darkTheme,
                onToggle = onToggleTheme,
                icon = if (settings.darkTheme) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                contentDescription = "Theme",
            )
            PillToggle(
                on = settings.showHeader,
                onToggle = onToggleHeader,
                textIndicator = "H",
                contentDescription = "Header",
            )
            PillToggle(
                on = settings.variant == LibrariesVariant.Traditional,
                onToggle = onToggleVariant,
                textIndicator = if (settings.variant == LibrariesVariant.Traditional) "A" else "C",
                contentDescription = "Variant",
            )
            if (!isMobile && onOpenGithub != null) {
                Spacer(Modifier.width(2.dp))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable(onClick = onOpenGithub),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Github,
                        contentDescription = "GitHub",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable(onClick = onOpenSettings),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }

    // Bottom divider
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant),
    )
}
