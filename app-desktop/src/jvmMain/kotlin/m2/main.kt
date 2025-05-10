package m2

import aboutlibraries.app_desktop.generated.resources.Res
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.rememberLibraries
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
fun main() = application {
    Window(title = "AboutLibraries Sample", onCloseRequest = ::exitApplication) {
        SampleTheme {
            Scaffold(
                topBar = { TopAppBar(title = { Text("AboutLibraries Compose Desktop Sample", maxLines = 1) }) }) {
                val libraries by rememberLibraries {
                    Res.readBytes("files/aboutlibraries.json").decodeToString()
                }
                LibrariesContainer(
                    libraries = libraries,
                    modifier = Modifier.fillMaxSize(),
                    showFundingBadges = true,
                    // divider = { Divider(modifier = Modifier.fillMaxWidth()) }
                )
            }
        }
    }
}
