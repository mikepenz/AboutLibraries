package m3

import aboutlibraries.app_desktop.generated.resources.Res
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.rememberLibraries
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
fun main() = application {
    Window(title = "AboutLibraries M3 Sample", onCloseRequest = ::exitApplication) {
        AppTheme {
            Scaffold(
                topBar = { TopAppBar(title = { Text("AboutLibraries Compose M3 Desktop Sample", maxLines = 1) }) }) {
                val libraries by rememberLibraries {
                    Res.readBytes("files/aboutlibraries.json").decodeToString()
                }
                LibrariesContainer(
                    libraries = libraries,
                    modifier = Modifier.fillMaxSize().padding(it),
                    showFundingBadges = true,
                    header = {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                Text("Hello Header", maxLines = 1)
                            }
                        }
                    },
                    divider = { HorizontalDivider() },
                    footer = {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                Text("Hello Footer", maxLines = 1)
                            }
                        }
                    }
                )
            }
        }
    }
}
