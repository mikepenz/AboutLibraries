package com.mikepenz.aboutlibraries.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Badge
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.util.author

/**
 * Displays all provided libraries in a simple list.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LibrariesContainer(
    aboutLibsJson: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showAuthor: Boolean = true,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true
) {
    val libraries = remember { mutableStateOf<Libs?>(null) }
    LaunchedEffect(libraries) {
        libraries.value = Libs.Builder().withJson(aboutLibsJson).build()
    }

    val libs = libraries.value?.libraries
    if (libs != null) {
        Libraries(
            libraries = libs,
            modifier,
            contentPadding,
            showAuthor,
            showVersion,
            showLicenseBadges
        )
    }
}

/**
 * Displays all provided libraries in a simple list.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Libraries(
    libraries: List<Library>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showAuthor: Boolean = true,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true,
) {
    LazyColumn(modifier, contentPadding = contentPadding) {
        items(libraries) { library ->
            Library(library, showAuthor, showVersion, showLicenseBadges) {}
        }
    }
}


@Composable
private fun Library(
    library: Library,
    showAuthor: Boolean = true,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true,
    onClick: () -> Unit
) {
    val typography = MaterialTheme.typography
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick.invoke()
            }
            .padding(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            )
    ) {
        Row(
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = library.name,
                style = typography.h6,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val version = library.artifactVersion
            if (version != null && showVersion) {
                Text(
                    version,
                    modifier = Modifier.padding(start = 8.dp),
                    style = typography.body2,
                    textAlign = TextAlign.Center
                )
            }
        }
        val author = library.author
        if (showAuthor && author.isNotBlank()) {
            Text(
                text = author,
                style = typography.body2,
            )
        }
        if (showLicenseBadges && library.licenses.isNotEmpty()) {
            Row(modifier = Modifier.padding(top = 8.dp)) {
                library.licenses.forEach {
                    Badge(
                        modifier = Modifier.padding(end = 4.dp),
                        backgroundColor = MaterialTheme.colors.primary
                    ) {
                        Text(text = it.name)
                    }
                }
            }
        }

    }
}