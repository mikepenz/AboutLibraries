package com.mikepenz.aboutlibraries.ui.compose.wear.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListItemScope
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.Card
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.LibraryDimensions
import com.mikepenz.aboutlibraries.ui.compose.LibraryPadding
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
 * @param header A lambda to define the header content of the list (optional).
 * @param divider A composable lambda to define a divider between library items (optional).
 * @param footer A lambda to define the footer content of the list (optional).
 * @param onLibraryClick A callback invoked when a library item is clicked. Returns `true` if the click is handled.
 */
@Composable
fun WearLibrariesScaffold(
    libraries: List<Library>,
    modifier: Modifier = Modifier,
    libraryModifier: Modifier = Modifier,
    lazyListState: ScalingLazyListState = rememberScalingLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    padding: LibraryPadding = LibraryDefaults.libraryPadding(),
    dimensions: LibraryDimensions = LibraryDefaults.libraryDimensions(),
    name: @Composable BoxScope.(name: String) -> Unit = {},
    version: (@Composable BoxScope.(version: String) -> Unit)? = null,
    author: (@Composable BoxScope.(authors: String) -> Unit)? = null,
    description: (@Composable BoxScope.(description: String) -> Unit)? = null,
    license: (@Composable FlowRowScope.(license: License) -> Unit)? = null,
    header: (ScalingLazyListScope.() -> Unit)? = null,
    divider: (@Composable ScalingLazyListItemScope.() -> Unit)? = null,
    footer: (ScalingLazyListScope.() -> Unit)? = null,
    onLibraryClick: ((Library) -> Boolean)? = { false },
) {
    val uriHandler = LocalUriHandler.current
    ScalingLazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensions.itemSpacing),
        state = lazyListState,
        contentPadding = contentPadding
    ) {
        header?.invoke(this)
        itemsIndexed(libraries) { index, library ->
            Card(
                modifier = libraryModifier.height(IntrinsicSize.Min),
                onClick = {
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
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(padding.verticalPadding, Alignment.CenterVertically),
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Row {
                        val authors = library.author
                        Box(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            if (author != null && authors.isNotBlank()) {
                                author(authors)
                            }
                        }
                        val artifactVersion = library.artifactVersion
                        if (version != null && artifactVersion != null) {
                            Box {
                                version(artifactVersion)
                            }
                        }
                    }

                    Box {
                        name(library.name)
                    }

                    val desc = library.description
                    if (description != null && !desc.isNullOrBlank()) {
                        Box {
                            description(desc)
                        }
                    }

                    if (license != null && library.licenses.isNotEmpty()) {
                        FlowRow {
                            library.licenses.forEach {
                                license(it)
                            }
                        }
                    }
                }
            }

            if (divider != null && index < libraries.lastIndex) {
                divider.invoke(this)
            }
        }
        footer?.invoke(this)
    }
}