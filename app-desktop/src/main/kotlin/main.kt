import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.util.strippedLicenseContent

fun main() = application {
    Window(title = "AboutLibraries Sample", onCloseRequest = ::exitApplication) {
        SampleTheme {
            Scaffold(
                topBar = { TopAppBar(title = { Text("AboutLibraries Compose Desktop Sample") }) }
            ) {
                val openDialog = remember { mutableStateOf<String?>(null) }

                LibrariesContainer(useResource("aboutlibraries.json") {
                    it.bufferedReader().readText()
                }, Modifier.fillMaxSize()) {
                    openDialog.value = it.licenses.firstOrNull()?.strippedLicenseContent ?: ""
                }

                if (openDialog.value != null) {
                    val scrollState = rememberScrollState()
                    Surface(
                        modifier = Modifier
                            .padding(8.dp)
                            .verticalScroll(scrollState)
                            .fillMaxSize()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = openDialog.value ?: "",
                            )
                            TextButton(
                                onClick = { openDialog.value = null },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("OK")
                            }
                        }
                    }
                }
            }
        }
    }
}
