package com.mikepenz.aboutlibraries.screenshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.layout.LibraryScaffoldLayout


@OptIn(ExperimentalLayoutApi::class)
@Preview
@Composable
fun LibraryScaffoldLayoutPreview() {
    LibraryScaffoldLayout(
        modifier = Modifier.background(Color.Blue),
        libraryPadding = LibraryDefaults.libraryPadding(),
        name = {
            BasicText(
                text = "Name",
                style = TextStyle.Default.copy(fontSize = 20.sp),
                modifier = Modifier
                    .background(Color.Red)
                    .fillMaxWidth()
            )
        },
        version = {
            BasicText(
                text = "Version",
                modifier = Modifier.background(Color.Green)
            )
        },
        author = {
            BasicText(
                text = "Author",
                modifier = Modifier.background(Color.Cyan)
            )
        },
        description = {
            BasicText(
                text = "Description Description Description Description Description",
                modifier = Modifier.background(Color.Magenta)
            )
        },
        licenses = {
            BasicText(
                text = "Apache 2.0",
                modifier = Modifier.background(Color.Yellow)
            )
            BasicText(
                text = "MIT",
                modifier = Modifier.background(Color.Yellow)
            )
        },
        actions = {
            BasicText(
                text = "Action",
                modifier = Modifier.background(Color.White)
            )
        }
    )
}