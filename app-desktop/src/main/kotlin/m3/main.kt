package m3

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.useResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.rememberLibraries

@OptIn(ExperimentalMaterial3Api::class)
fun main() = application {
    Window(title = "AboutLibraries M3 Sample", onCloseRequest = ::exitApplication) {
        AppTheme {
            Scaffold(
                topBar = { TopAppBar(title = { Text("AboutLibraries Compose M3 Desktop Sample") }) }
            ) {
                val libraries by rememberLibraries {
                    useResource("aboutlibraries.json") { res -> res.bufferedReader().readText() }
                }
                LibrariesContainer(libraries, Modifier.fillMaxSize().padding(it))
            }
        }
    }
}
