package com.mikepenz.aboutlibraries.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Funding
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.ui.compose.layout.LibraryScaffoldLayout
import com.mikepenz.aboutlibraries.ui.compose.util.author

/**
 * A composable function that displays a scaffolded list of libraries with customizable content.
 *
 * @param libraries The list of libraries to display.
 * @param modifier Modifier to be applied to the LazyColumn.
 * @param libraryModifier Modifier to be applied to each library item.
 * @param lazyListState The state object to control or observe the LazyColumn's scroll state.
 * @param contentPadding Padding values to be applied around the content.
 * @param padding Padding configuration for each library item.
 * @param dimensions Dimensions configuration for spacing and layout.
 * @param name A composable lambda to display the library name.
 * @param version A composable lambda to display the library version (optional).
 * @param author A composable lambda to display the library author(s) (optional).
 * @param description A composable lambda to display the library description (optional).
 * @param license A composable lambda to display the library licenses (optional).
 * @param funding A composable lambda to display the library funding information (optional).
 * @param actions A composable lambda to display additional actions for each library (optional).
 * @param header A lambda to define the header content of the list (optional).
 * @param divider A composable lambda to define a divider between library items (optional).
 * @param footer A lambda to define the footer content of the list (optional).
 * @param onLibraryClick A callback invoked when a library item is clicked. Returns `true` if the click is handled.
 */
@Composable
fun LibrariesScaffold(
    libraries: List<Library>,
    modifier: Modifier = Modifier,
    libraryModifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    padding: LibraryPadding = LibraryDefaults.libraryPadding(),
    dimensions: LibraryDimensions = LibraryDefaults.libraryDimensions(),
    name: @Composable BoxScope.(name: String) -> Unit = {},
    version: (@Composable BoxScope.(version: String) -> Unit)? = null,
    author: (@Composable BoxScope.(authors: String) -> Unit)? = null,
    description: (@Composable BoxScope.(description: String) -> Unit)? = null,
    license: (@Composable FlowRowScope.(license: License) -> Unit)? = null,
    funding: (@Composable FlowRowScope.(funding: Funding) -> Unit)? = null,
    actions: (@Composable FlowRowScope.(library: Library) -> Unit)? = null,
    header: (LazyListScope.() -> Unit)? = null,
    divider: (@Composable LazyItemScope.() -> Unit)? = null,
    footer: (LazyListScope.() -> Unit)? = null,
    onLibraryClick: ((Library) -> Boolean)? = { false },
) {
    val uriHandler = LocalUriHandler.current
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensions.itemSpacing),
        state = lazyListState,
        contentPadding = contentPadding
    ) {
        header?.invoke(this)
        itemsIndexed(libraries) { index, library ->
            LibraryScaffoldLayout(
                modifier = libraryModifier.clickable {
                    val license = library.licenses.firstOrNull()
                    val handled = onLibraryClick?.invoke(library) ?: false

                    if (!handled && !license?.url.isNullOrBlank()) {
                        license.url?.also {
                            try {
                                uriHandler.openUri(it)
                            } catch (t: Throwable) {
                                println("Failed to open url: $it // ${t.message}")
                            }
                        }
                    }
                },
                libraryPadding = padding,
                name = { name(library.name) },
                version = {
                    val artifactVersion = library.artifactVersion
                    if (version != null && artifactVersion != null) {
                        version(artifactVersion)
                    }
                },
                author = {
                    val authors = library.author
                    if (author != null && authors.isNotBlank()) {
                        author(authors)
                    }
                },
                description = {
                    val desc = library.description
                    if (description != null && !desc.isNullOrBlank()) {
                        description(desc)
                    }
                },
                licenses = {
                    if (license != null && library.licenses.isNotEmpty()) {
                        library.licenses.forEach {
                            license(it)
                        }
                    }
                },
                actions = {
                    if (funding != null && library.funding.isNotEmpty()) {
                        library.funding.forEach {
                            funding(it)
                        }
                    }
                    if (actions != null) {
                        actions(library)
                    }
                }
            )

            if (divider != null && index < libraries.lastIndex) {
                divider.invoke(this)
            }
        }
        footer?.invoke(this)
    }
}