package com.mikepenz.aboutlibraries.sample

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.sample.sample.HeaderPosition
import com.mikepenz.aboutlibraries.sample.sample.HeaderStyle
import com.mikepenz.aboutlibraries.sample.sample.LicenseFilterBar
import com.mikepenz.aboutlibraries.sample.sample.LicenseFilterTab
import com.mikepenz.aboutlibraries.sample.sample.SampleHeader
import com.mikepenz.aboutlibraries.sample.sample.SampleSettings
import com.mikepenz.aboutlibraries.sample.sample.SettingsPanel
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.style.m3LibrariesStyle
import com.mikepenz.aboutlibraries.ui.compose.m3.style.m3VariantColors
import com.mikepenz.aboutlibraries.ui.compose.style.ContrastLevel
import com.mikepenz.aboutlibraries.ui.compose.style.DefaultLibraryStrings
import com.mikepenz.aboutlibraries.ui.compose.style.m2LibrariesStyle
import com.mikepenz.aboutlibraries.ui.compose.style.m2VariantColors
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrariesVariant
import com.mikepenz.aboutlibraries.ui.compose.variant.refined.LicenseTab
import com.mikepenz.aboutlibraries.ui.compose.variant.refined.RefinedHeader
import com.mikepenz.aboutlibraries.ui.compose.variant.traditional.TraditionalHeader
import androidx.compose.material.MaterialTheme as M2MaterialTheme
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer as M2LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer as M3LibrariesContainer

private const val TabletBreakpointDp = 600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(libs: Libs?) {
    val systemDark = isSystemInDarkTheme()
    var settings by remember { mutableStateOf(SampleSettings(darkTheme = systemDark)) }
    var showSettings by remember { mutableStateOf(false) }
    var licenseFilter by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(licenseFilter, libs) {
        if (libs != null && licenseFilter != null && libs.libraries.none { lib ->
                lib.licenses.any { it.spdxId == licenseFilter || it.name == licenseFilter }
            }
        ) licenseFilter = null
    }

    val filteredLibs = remember(libs, licenseFilter, searchQuery) {
        libs?.libraries.orEmpty()
            .let { all ->
                if (licenseFilter == null) all
                else all.filter { lib ->
                    lib.licenses.any { it.spdxId == licenseFilter || it.name == licenseFilter }
                }
            }
            .let { list ->
                val q = searchQuery.trim()
                if (q.isEmpty()) list
                else list.filter { lib ->
                    lib.name.contains(q, ignoreCase = true) ||
                        lib.developers.any { it.name?.contains(q, ignoreCase = true) == true } ||
                        lib.description?.contains(q, ignoreCase = true) == true ||
                        lib.licenses.any { it.name.contains(q, ignoreCase = true) }
                }
            }
    }
    val tabs = remember(libs) {
        val grouped = libs?.libraries.orEmpty()
            .flatMap { it.licenses }
            .groupBy { it.spdxId ?: it.name }
        buildList {
            add(LicenseFilterTab(spdxId = null, label = "All", count = libs?.libraries.orEmpty().size))
            grouped.entries.sortedByDescending { it.value.size }.forEach { (id, list) ->
                add(LicenseFilterTab(spdxId = id, label = list.first().name, count = list.size))
            }
        }
    }

    val animatedAccent by animateColorAsState(
        targetValue = settings.accent,
        animationSpec = tween(durationMillis = 600),
    )

    AppTheme(useV3 = settings.useMaterial3, useDarkTheme = settings.darkTheme, accent = animatedAccent) {
        val fullStyle = if (settings.useMaterial3) LibraryDefaults.m3LibrariesStyle(compact = false) else LibraryDefaults.m2LibrariesStyle(compact = false)
        val compactStyle = if (settings.useMaterial3) LibraryDefaults.m3LibrariesStyle(compact = true) else LibraryDefaults.m2LibrariesStyle(compact = true)
        val fullAppIcon: @Composable () -> Unit = if (settings.useMaterial3) {
            {
                AppIconBadge(
                    letter = "A",
                    background = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                    cornerRadius = 12.dp,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        } else {
            {
                AppIconBadge(
                    letter = "A",
                    background = M2MaterialTheme.colors.primary.copy(alpha = 0.16f),
                    contentColor = M2MaterialTheme.colors.primary,
                    cornerRadius = 12.dp,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        val compactAppIcon: @Composable () -> Unit = if (settings.useMaterial3) {
            {
                AppIconBadge(
                    letter = "A",
                    background = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    cornerRadius = 7.dp,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        } else {
            {
                AppIconBadge(
                    letter = "A",
                    background = M2MaterialTheme.colors.primary,
                    contentColor = M2MaterialTheme.colors.onPrimary,
                    cornerRadius = 7.dp,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        val uriHandler = LocalUriHandler.current

        BoxWithConstraints(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
            val isMobile = maxWidth.value < TabletBreakpointDp
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(
                        WindowInsets.systemBars.only(
                            WindowInsetsSides.Horizontal + WindowInsetsSides.Top,
                        ),
                    ),
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    SampleHeader(
                        settings = settings,
                        isMobile = isMobile,
                        onToggleTheme = { settings = settings.copy(darkTheme = !settings.darkTheme) },
                        onToggleHeader = { settings = settings.copy(showHeader = !settings.showHeader) },
                        onToggleVariant = {
                            settings = settings.copy(
                                variant = if (settings.variant == LibrariesVariant.Traditional) LibrariesVariant.Refined
                                else LibrariesVariant.Traditional,
                            )
                        },
                        onOpenGithub = { uriHandler.openUri("https://github.com/mikepenz/AboutLibraries") },
                        onOpenSettings = { showSettings = true },
                        appName = "AboutLibraries",
                        appVersion = VERSION,
                    )

                    if (settings.showHeader && settings.headerPosition == HeaderPosition.Fixed) {
                        when (settings.headerStyle) {
                            HeaderStyle.Full -> TraditionalHeader(
                                title = "AboutLibraries",
                                tagline = "Open source acknowledgements",
                                versionLabel = "v$VERSION",
                                style = fullStyle,
                                strings = DefaultLibraryStrings,
                                appIcon = fullAppIcon,
                                showSearch = settings.showSearch,
                                searchQuery = searchQuery,
                                onSearchChange = { searchQuery = it },
                            )

                            HeaderStyle.Compact -> RefinedHeader(
                                title = "AboutLibraries",
                                subtitle = "v$VERSION · ${libs?.libraries.orEmpty().size} libraries",
                                style = compactStyle,
                                strings = DefaultLibraryStrings,
                                tabs = if (settings.showTabsInHeader) tabs.map {
                                    LicenseTab(it.spdxId, it.label, it.count)
                                } else emptyList(),
                                selectedTab = licenseFilter,
                                onTabSelected = { licenseFilter = it },
                                appIcon = compactAppIcon,
                                showSearch = settings.showSearch,
                                searchQuery = searchQuery,
                                onSearchChange = { searchQuery = it },
                                inlineSearch = true,
                            )
                        }
                    }

                    val showFilterBar = settings.showLicenseFilter
                        && (!settings.showHeader || settings.headerStyle != HeaderStyle.Compact || !settings.showTabsInHeader)
                    val showFilterBarFixed = showFilterBar && settings.headerPosition == HeaderPosition.Fixed
                    if (showFilterBarFixed) {
                        LicenseFilterBar(
                            tabs = tabs,
                            selectedSpdxId = licenseFilter,
                            onSelect = { licenseFilter = it },
                            isMobile = isMobile,
                        )
                    }

                    val needsListHeader = settings.headerPosition != HeaderPosition.Fixed
                        && (settings.showHeader || showFilterBar)
                    val headerLambda: (LazyListScope.() -> Unit)? = if (needsListHeader) {
                        {
                            val headerContent: @Composable () -> Unit = {
                                when (settings.headerStyle) {
                                    HeaderStyle.Full -> TraditionalHeader(
                                        title = "AboutLibraries",
                                        tagline = "Open source acknowledgements",
                                        versionLabel = "v$VERSION",
                                        style = fullStyle,
                                        strings = DefaultLibraryStrings,
                                        appIcon = fullAppIcon,
                                        showSearch = settings.showSearch,
                                        searchQuery = searchQuery,
                                        onSearchChange = { searchQuery = it },
                                    )

                                    HeaderStyle.Compact -> RefinedHeader(
                                        title = "AboutLibraries",
                                        subtitle = "v$VERSION · ${libs?.libraries.orEmpty().size} libraries",
                                        style = compactStyle,
                                        strings = DefaultLibraryStrings,
                                        tabs = if (settings.showTabsInHeader && settings.showLicenseFilter) tabs.map {
                                            LicenseTab(it.spdxId, it.label, it.count)
                                        } else emptyList(),
                                        selectedTab = licenseFilter,
                                        onTabSelected = { licenseFilter = it },
                                        appIcon = compactAppIcon,
                                        showSearch = settings.showSearch,
                                        searchQuery = searchQuery,
                                        onSearchChange = { searchQuery = it },
                                        inlineSearch = true,
                                    )
                                }
                            }
                            if (settings.showHeader) {
                                if (settings.headerPosition == HeaderPosition.Sticky) {
                                    stickyHeader { headerContent() }
                                } else {
                                    item { headerContent() }
                                }
                            }
                            val showFilterBarInList = showFilterBar
                            if (showFilterBarInList) {
                                if (settings.headerPosition == HeaderPosition.Sticky) {
                                    stickyHeader {
                                        LicenseFilterBar(
                                            tabs = tabs,
                                            selectedSpdxId = licenseFilter,
                                            onSelect = { licenseFilter = it },
                                            isMobile = isMobile,
                                        )
                                    }
                                } else {
                                    item {
                                        LicenseFilterBar(
                                            tabs = tabs,
                                            selectedSpdxId = licenseFilter,
                                            onSelect = { licenseFilter = it },
                                            isMobile = isMobile,
                                        )
                                    }
                                }
                            }
                        }
                    } else null

                    val contrastLevel = if (settings.highContrast) ContrastLevel.High else ContrastLevel.Normal
                    if (settings.useMaterial3) {
                        M3LibrariesContainer(
                            libraries = libs?.let { Libs(filteredLibs, it.licenses) },
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            badges = settings.badges,
                            actionLabels = settings.actionLabels,
                            variant = settings.variant,
                            density = settings.density,
                            detailMode = settings.detailMode,
                            actionMode = settings.actionMode,
                            variantColors = LibraryDefaults.m3VariantColors(contrastLevel = contrastLevel),
                            header = headerLambda,
                        )
                    } else {
                        M2LibrariesContainer(
                            libraries = libs?.let { Libs(filteredLibs, it.licenses) },
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            badges = settings.badges,
                            actionLabels = settings.actionLabels,
                            variant = settings.variant,
                            density = settings.density,
                            detailMode = settings.detailMode,
                            actionMode = settings.actionMode,
                            variantColors = LibraryDefaults.m2VariantColors(contrastLevel = contrastLevel),
                            header = headerLambda,
                        )
                    }
                }

            }

            // Desktop / tablet: side drawer floats on the right edge with scrim behind
            if (!isMobile && showSettings) {
                val focusRequester = remember { FocusRequester() }
                LaunchedEffect(focusRequester) { focusRequester.requestFocus() }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .focusRequester(focusRequester)
                        .focusable()
                        .onPreviewKeyEvent { event ->
                            if (event.type == KeyEventType.KeyDown && event.key == Key.Escape) {
                                showSettings = false
                                true
                            } else false
                        },
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f))
                            .clickable { showSettings = false },
                    )
                    SettingsPanel(
                        settings = settings,
                        onChange = { settings = it },
                        onClose = { showSettings = false },
                        isMobile = false,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight(),
                    )
                }
            }

            // Mobile: bottom sheet
            if (isMobile && showSettings) {
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
                ModalBottomSheet(
                    onDismissRequest = { showSettings = false },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    SettingsPanel(
                        settings = settings,
                        onChange = { settings = it },
                        onClose = { showSettings = false },
                        isMobile = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun AppIconBadge(
    letter: String,
    background: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onPrimaryContainer,
    cornerRadius: androidx.compose.ui.unit.Dp = 12.dp,
    fontSize: androidx.compose.ui.unit.TextUnit = 20.sp,
    fontWeight: FontWeight = FontWeight.SemiBold,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = background,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(cornerRadius),
            ),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.material3.Text(
            text = letter,
            color = contentColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
        )
    }
}
