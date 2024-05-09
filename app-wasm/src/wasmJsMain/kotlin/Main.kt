import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.window.CanvasBasedWindow
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.LicenseDialog
import com.mikepenz.aboutlibraries.ui.compose.util.htmlReadyLicenseContent
import com.mikepenz.app_wasm.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalComposeUiApi::class, ExperimentalResourceApi::class)
fun main() {
    CanvasBasedWindow("AboutLibraries", canvasElementId = "aboutLibsCanvas") {
        SampleTheme {
            Scaffold(
                topBar = { TopAppBar(title = { Text("AboutLibraries Compose Desktop Sample") }) }
            ) {
                val libraries = produceState<Libs?>(null) {
                    value = withContext(Dispatchers.Default) {
                        Libs.Builder()
                            .withJson(Res.readBytes("files/aboutlibraries.json").decodeToString())
                            .build()
                    }
                }

                val uriHandler = LocalUriHandler.current
                val openDialog = remember { mutableStateOf<Library?>(null) }

                LibrariesContainer(
                    libraries.value,
                    Modifier.fillMaxSize(),
                    onLibraryClick = { library ->
                        val license = library.licenses.firstOrNull()
                        if (!license?.url.isNullOrBlank()) {
                            license?.url?.also {
                                try {
                                    uriHandler.openUri(it)
                                } catch (t: Throwable) {
                                    println("Failed to open url: ${it}")
                                }
                            }
                        } else if (!license?.htmlReadyLicenseContent.isNullOrBlank()) {
                            openDialog.value = library
                        }
                    },
                )


                val library = openDialog.value
                if (library != null) {
                    // TODO use https://android-review.googlesource.com/c/platform/frameworks/support/+/3024604
                    LicenseDialog(library, body = {
                        Text(library.licenses.firstOrNull()?.htmlReadyLicenseContent ?: "")
                    }) {
                        openDialog.value = null
                    }
                }
            }
        }
    }
}