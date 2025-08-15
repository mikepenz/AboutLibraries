import aboutlibraries.app.generated.resources.Res
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeViewport
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.rememberLibraries
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalComposeUiApi::class, ExperimentalResourceApi::class)
fun main() {
    ComposeViewport {
        SampleTheme {
            Scaffold(
                topBar = { TopAppBar(title = { Text("AboutLibraries Compose Desktop Sample") }) }
            ) {
                val libraries by rememberLibraries {
                    Res.readBytes("files/aboutlibraries.json").decodeToString()
                }
                LibrariesContainer(
                    libraries,
                    Modifier.fillMaxSize(),
                )
            }
        }
    }
}