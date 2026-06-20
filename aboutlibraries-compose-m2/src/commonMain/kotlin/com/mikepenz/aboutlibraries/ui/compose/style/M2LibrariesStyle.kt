package com.mikepenz.aboutlibraries.ui.compose.style

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults

/**
 * Returns a [LibrariesStyle] wired to Material 2 theme tokens.
 *
 * @param compact `false` (default) produces a full / traditional style: large icon (44 dp),
 *   prominent title, standard padding. `true` produces a compact / refined style: small icon
 *   (28 dp), semibold inline title, tighter padding, rounded search field.
 */
@Composable
fun LibraryDefaults.m2LibrariesStyle(compact: Boolean = false): LibrariesStyle {
    return if (compact) {
        librariesStyle(
            colors = m2VariantColors(
                headerBackground = MaterialTheme.colors.background,
            ),
            textStyles = m2VariantTextStyles(
                headerTitleTextStyle = MaterialTheme.typography.subtitle2.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.sp,
                ),
                headerTaglineTextStyle = MaterialTheme.typography.caption.copy(fontSize = 11.sp),
            ),
            padding = remember {
                defaultVariantPadding(
                    headerPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                )
            },
            dimensions = remember {
                defaultVariantDimensions(
                    headerIconSize = 28.dp,
                    searchHeight = 30.dp,
                )
            },
            shapes = remember {
                defaultVariantShapes(
                    headerSearchShape = RoundedCornerShape(8.dp),
                )
            },
        )
    } else {
        librariesStyle(
            colors = m2VariantColors(
                headerBackground = MaterialTheme.colors.surface,
            ),
            textStyles = m2VariantTextStyles(
                headerTitleTextStyle = MaterialTheme.typography.h6.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = (-0.2).sp,
                ),
            ),
            padding = remember {
                defaultVariantPadding(
                    headerPadding = PaddingValues(start = 22.dp, top = 18.dp, end = 22.dp, bottom = 16.dp),
                )
            },
            dimensions = remember {
                defaultVariantDimensions(
                    headerIconSize = 44.dp,
                    searchHeight = 40.dp,
                )
            },
        )
    }
}
