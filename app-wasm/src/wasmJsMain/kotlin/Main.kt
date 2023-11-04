import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.produceState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.CanvasBasedWindow
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.util.StableLibs
import com.mikepenz.aboutlibraries.ui.compose.util.stable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource

@OptIn(ExperimentalComposeUiApi::class, ExperimentalResourceApi::class)
fun main() {
    CanvasBasedWindow("AboutLibraries", canvasElementId = "aboutLibsCanvas") {
        SampleTheme {
            Scaffold(
                topBar = { TopAppBar(title = { Text("AboutLibraries Compose Desktop Sample") }) }
            ) {
                val libraries = produceState<StableLibs?>(null) {
                    value = withContext(Dispatchers.Default) {
                        Libs.Builder()
                            .withJson(resource("aboutlibraries.json").readBytes().decodeToString())
                            .build().stable
                    }
                }

                LibrariesContainer(
                    libraries.value,
                    Modifier.fillMaxSize()
                )
            }
        }
    }
}