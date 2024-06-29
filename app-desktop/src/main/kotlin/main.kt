import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.useResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.rememberLibraries
import m2.SampleTheme

fun main() = application {
    Window(title = "AboutLibraries Sample", onCloseRequest = ::exitApplication) {
        SampleTheme {
            Scaffold(
                topBar = { TopAppBar(title = { Text("AboutLibraries Compose Desktop Sample") }) }
            ) {
                val libraries by rememberLibraries {
                    useResource("aboutlibraries.json") { res -> res.bufferedReader().readText() }
                }
                LibrariesContainer(libraries, Modifier.fillMaxSize())
            }
        }
    }
}
