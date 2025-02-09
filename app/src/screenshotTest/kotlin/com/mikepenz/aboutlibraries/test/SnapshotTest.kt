package com.mikepenz.aboutlibraries.test

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mikepenz.aboutlibraries.test.data.fakeData
import com.mikepenz.aboutlibraries.ui.compose.m3.Libraries

class SnapshotScreenshots {

    @Preview("Library items (Default)")
    @Composable
    fun PreviewLibraries() {
        MaterialTheme {
            Surface {
                Libraries(
                    fakeData.libraries,
                    showDescription = true
                )
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
}