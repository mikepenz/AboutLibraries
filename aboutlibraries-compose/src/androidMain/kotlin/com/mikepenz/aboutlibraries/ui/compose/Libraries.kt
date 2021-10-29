import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library

/**
 */
@Composable
fun Libraries(libraries: List<Library>, modifier: Modifier = Modifier) {
    LazyColumn(modifier) {
        items(libraries) { library ->
            Library(library)
        }
    }
}


@Composable
private fun Library(library: Library) {
    val openDialog = remember { mutableStateOf(false) }
    val typography = MaterialTheme.typography
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                openDialog.value = true
            }
            .padding(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            )
    ) {
        Text(
            text = library.name,
            style = typography.h5,
            modifier = Modifier.padding(top = 4.dp)
        )
    }

    if (openDialog.value) {
        AlertDialog(
            text = {
                Text(text = library.licenses.first().licenseContent ?: "")
            },
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = { openDialog.value = false }) {
                    Text("OK")
                }
            },
            dismissButton = {}
        )
    }
}

@Preview("Library item")
@Composable
fun PreviewPost() {
    MaterialTheme {
        Surface {
            Library(
                Library(
                    "", "", "MaterialDrawer", "", "", emptyList(), null, null
                )
            )
        }
    }
}