package com.mikepenz.aboutlibraries.screenshot.m2

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.mikepenz.aboutlibraries.screenshot.fakeLibraries
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.style.LicenseHueResolver
import com.mikepenz.aboutlibraries.ui.compose.style.m2VariantColors
import com.mikepenz.aboutlibraries.ui.compose.style.m2VariantTextStyles
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryBadges

@Composable
fun Theme(isDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) = MaterialTheme(if (isDarkTheme) darkColors() else lightColors()) { content() }

@PreviewLightDark
@Composable
fun PreviewLibraries() = Theme {
    Surface {
        LibrariesContainer(libraries = fakeLibraries, badges = LibraryBadges(description = true), modifier = Modifier.width(360.dp))
    }
}

@PreviewLightDark
@Composable
fun PreviewLibrariesOff() = Theme {
    Surface {
        LibrariesContainer(fakeLibraries, badges = LibraryBadges(author = false, license = false), modifier = Modifier.width(360.dp))
    }
}

@PreviewLightDark
@Composable
fun PreviewLibrariesCustomTextStyles() = Theme {
    Surface {
        LibrariesContainer(
            libraries = fakeLibraries,
            modifier = Modifier.width(360.dp),
            variantTextStyles = LibraryDefaults.m2VariantTextStyles(
                nameTextStyle = MaterialTheme.typography.subtitle1.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                versionTextStyle = MaterialTheme.typography.body1.copy(fontSize = 14.sp),
            ),
        )
    }
}

/**
 * The v14 look: static, solid `primary` license chips instead of the v15 accent-derived tint.
 * Documented in the README under "Static license chip colors".
 */
@PreviewLightDark
@Composable
fun PreviewLibrariesStaticLicenseChips() = Theme {
    Surface {
        LibrariesContainer(
            libraries = fakeLibraries,
            modifier = Modifier.width(360.dp),
            variantColors = LibraryDefaults.m2VariantColors(
                licenseHueResolver = LicenseHueResolver.None,
                licenseBadgeContainer = MaterialTheme.colors.primary,
                licenseBadgeContent = MaterialTheme.colors.onPrimary,
            ),
        )
    }
}

@PreviewLightDark
@Composable
fun PreviewLibraryRTL() = Theme {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Surface {
            LibrariesContainer(fakeLibraries, modifier = Modifier.width(360.dp))
        }
    }
}
