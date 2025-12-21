@file:Suppress("DEPRECATION")

package com.mikepenz.aboutlibraries.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomDrawer
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.material.ripple
import androidx.compose.material3.Badge
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.LibsConfiguration
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.sample.icons.Github
import com.mikepenz.aboutlibraries.sample.legacy.R
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.util.SpecialButton
import kotlinx.coroutines.launch

class ComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MainLayout()
        }
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainLayout() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        var showAuthor by remember { mutableStateOf(true) }
        var showDescription by remember { mutableStateOf(false) }
        var showVersion by remember { mutableStateOf(true) }
        var showLicenseBadges by remember { mutableStateOf(true) }
        var showHeader by remember { mutableStateOf(false) }

        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val bottomDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)
        val libraries by produceLibraries(R.raw.aboutlibraries)

        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        ModalNavigationDrawer(scrimColor = Color.Transparent, drawerState = drawerState, drawerContent = {
            DismissibleDrawerSheet(
                drawerState = drawerState,
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .fillMaxHeight()
                            .weight(1f)
                    ) {
                        DrawerItems()
                    }
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.action_opensource)) },
                        selected = false,
                        icon = { Icon(Github, "Open Source") },
                        onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, "https://github.com/mikepenz/AboutLibraries".toUri())) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }, content = {
            BottomDrawer(gesturesEnabled = bottomDrawerState.isOpen, drawerState = bottomDrawerState, drawerContent = {
                Column(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
                ) {
                    ToggleableSetting(
                        title = "Show Author",
                        icon = Icons.Default.Person,
                        enabled = showAuthor,
                        onToggled = { showAuthor = it },
                    )
                    ToggleableSetting(
                        title = "Show Description",
                        icon = Icons.Default.Person,
                        enabled = showDescription,
                        onToggled = { showDescription = it },
                    )
                    ToggleableSetting(
                        title = "Show Version",
                        icon = Icons.Default.Build,
                        enabled = showVersion,
                        onToggled = { showVersion = it },
                    )
                    ToggleableSetting(
                        title = "Show License Badges",
                        icon = Icons.AutoMirrored.Filled.List,
                        enabled = showLicenseBadges,
                        onToggled = { showLicenseBadges = it },
                    )
                    ToggleableSetting(
                        title = "Show Header",
                        icon = Icons.Default.Info,
                        enabled = showHeader,
                        onToggled = { showHeader = it },
                    )
                }
            }, content = {
                Scaffold(
                    topBar = {
                        // We use TopAppBar from accompanist-insets-ui which allows us to provide
                        // content padding matching the system bars insets.
                        TopAppBar(
                            title = { Text("AboutLibs") }, navigationIcon = {
                                IconButton(onClick = {
                                    scope.launch {
                                        if (drawerState.isOpen) {
                                            drawerState.close()
                                        } else {
                                            drawerState.open()
                                        }
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Menu, contentDescription = "Open Menu"
                                    )
                                }
                            }, colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                            ), actions = {
                                IconButton(onClick = { scope.launch { bottomDrawerState.open() } }) {
                                    Icon(Icons.Default.Settings, "Settings")
                                }
                            })
                    },
                ) { contentPadding ->
                    LibrariesContainer(
                        libraries = libraries,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = contentPadding,
                        showAuthor = showAuthor,
                        showDescription = showDescription,
                        showVersion = showVersion,
                        showLicenseBadges = showLicenseBadges,
                        header = {
                            if (showHeader) {
                                stickyHeader {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.surface)
                                            .padding(vertical = 25.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Text("ExampleHeader")
                                    }
                                }
                            }
                        }
                    )
                }
            })
        })
    }
}

@Composable
fun ToggleableSetting(title: String, icon: ImageVector, enabled: Boolean, onToggled: (Boolean) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(interactionSource = interactionSource, onClick = { onToggled(!enabled) }, indication = ripple())
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        Icon(icon, contentDescription = title)
        Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(interactionSource = interactionSource, checked = enabled, onCheckedChange = {
            onToggled(!enabled)
        })
    }
}

@Composable
private fun ColumnScope.DrawerItems() {
    val context = LocalContext.current
    Spacer(Modifier.height(12.dp))
    NavigationDrawerItem(
        label = { Text(stringResource(R.string.action_fargmentactivity)) },
        badge = { Badge { Text("Deprecated") } },
        selected = false,
        onClick = { context.startActivity(Intent(context, FragmentActivity::class.java)) },
        modifier = Modifier.padding(horizontal = 12.dp)
    )
    Spacer(Modifier.height(12.dp))
    NavigationDrawerItem(
        label = { Text(stringResource(R.string.action_manifestactivity)) }, badge = { Badge { Text("Deprecated") } }, selected = false, onClick = {
            val libsUIListener: LibsConfiguration.LibsUIListener = object : LibsConfiguration.LibsUIListener {
                override fun preOnCreateView(view: View): View {
                    return view
                }

                override fun postOnCreateView(view: View): View {
                    return view
                }
            }
            val libsListener: LibsConfiguration.LibsListener = object : LibsConfiguration.LibsListener {
                override fun onIconClicked(v: View) {
                    Toast.makeText(v.context, "We are able to track this now ;)", Toast.LENGTH_LONG).show()
                }

                override fun onLibraryAuthorClicked(v: View, library: Library): Boolean {
                    return false
                }

                override fun onLibraryContentClicked(v: View, library: Library): Boolean {
                    return false
                }

                override fun onLibraryBottomClicked(v: View, library: Library): Boolean {
                    return false
                }

                override fun onExtraClicked(v: View, specialButton: SpecialButton): Boolean {
                    return false
                }

                override fun onIconLongClicked(v: View): Boolean {
                    return false
                }

                override fun onLibraryAuthorLongClicked(v: View, library: Library): Boolean {
                    return false
                }

                override fun onLibraryContentLongClicked(v: View, library: Library): Boolean {
                    return false
                }

                override fun onLibraryBottomLongClicked(v: View, library: Library): Boolean {
                    return false
                }
            }

            LibsBuilder().withLicenseShown(true).withVersionShown(true).withActivityTitle("Open Source").withEdgeToEdge(true).withListener(libsListener)
                .withUiListener(libsUIListener)
                .withSearchEnabled(true).start(context)
        }, modifier = Modifier.padding(horizontal = 12.dp)
    )
    Spacer(Modifier.height(12.dp))
    NavigationDrawerItem(
        label = { Text(stringResource(R.string.action_minimalactivity)) }, badge = { Badge { Text("Deprecated") } }, selected = false, onClick = {
            LibsBuilder().withAboutMinimalDesign(true).withEdgeToEdge(true).withActivityTitle("Open Source").withAboutIconShown(false).withSearchEnabled(true).start(context)
        }, modifier = Modifier.padding(horizontal = 12.dp)
    )
    Spacer(Modifier.height(12.dp))
    NavigationDrawerItem(
        label = { Text(stringResource(R.string.action_extendactivity)) },
        badge = { Badge { Text("Deprecated") } },
        selected = false,
        onClick = { context.startActivity(Intent(context, ExtendActivity::class.java)) },
        modifier = Modifier.padding(horizontal = 12.dp)
    )
    Spacer(Modifier.height(12.dp))
    NavigationDrawerItem(
        label = { Text(stringResource(R.string.action_customsortactivity)) },
        badge = { Badge { Text("Deprecated") } },
        selected = false,
        onClick = { context.startActivity(Intent(context, CustomSortActivity::class.java)) },
        modifier = Modifier.padding(horizontal = 12.dp)
    )
}