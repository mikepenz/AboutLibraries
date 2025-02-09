package com.mikepenz.aboutlibraries.ui.compose.m3

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.m3.data.fakeData
import com.mikepenz.aboutlibraries.util.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Displays all provided libraries in a simple list.
 */
@Composable
fun LibrariesContainer(
    modifier: Modifier = Modifier,
    librariesBlock: (Context) -> Libs = { context ->
        Libs.Builder().withContext(context).build()
    },
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showAuthor: Boolean = true,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true,
    colors: LibraryColors = LibraryDefaults.libraryColors(),
    padding: LibraryPadding = LibraryDefaults.libraryPadding(),
    itemContentPadding: PaddingValues = LibraryDefaults.ContentPadding,
    itemSpacing: Dp = LibraryDefaults.LibraryItemSpacing,
    header: (LazyListScope.() -> Unit)? = null,
    onLibraryClick: ((Library) -> Unit)? = null,
) {
    val context = LocalContext.current

    val libraries = produceState<Libs?>(null) {
        value = withContext(Dispatchers.IO) {
            librariesBlock(context)
        }
    }
    LibrariesContainer(
        libraries.value,
        modifier,
        lazyListState,
        contentPadding,
        showAuthor,
        showVersion,
        showLicenseBadges,
        colors,
        padding,
        itemContentPadding,
        itemSpacing,
        header,
        onLibraryClick
    )
}

@Preview("Library items (Default)")
@Composable
fun PreviewLibraries() {
    MaterialTheme {
        Surface {
            Libraries(fakeData.libraries)
        }
    }
}


@Preview("Library items (Off)")
@Composable
fun PreviewLibrariesOff() {
    MaterialTheme {
        Surface {
            Libraries(fakeData.libraries, showAuthor = false, showLicenseBadges = false)
        }
    }
}

@Preview("Library item")
@Composable
fun PreviewLibrary() {
    MaterialTheme {
        Surface {
            Library(
                fakeData.libraries.first()
            ) {
                // on-click
            }
        }
    }
}
