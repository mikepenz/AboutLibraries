package com.mikepenz.aboutlibraries.ui

import android.os.Looper
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mikepenz.aboutlibraries.entity.Developer
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.style.defaultVariantColors
import com.mikepenz.aboutlibraries.ui.compose.style.librariesStyle
import com.mikepenz.aboutlibraries.ui.compose.variant.Libraries
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrariesVariant
import dejavu.assertRecompositions
import dejavu.assertStable
import dejavu.createRecompositionTrackingRule
import dejavu.resetRecompositionCounts
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.model.Statement

/**
 * Verifies that the [Libraries] list composable is stable:
 * - does not recompose when the same list reference is re-assigned
 * - does recompose when the list content genuinely changes
 */
@RunWith(AndroidJUnit4::class)
class LibrariesListStabilityTest {

    /**
     * DejaVu 0.3.1: Runtime.seedActiveActivity calls Choreographer.getInstance() on the
     * instrumentation thread. AndroidJUnitRunner does not prepare a Looper on that thread,
     * so the very first access to Choreographer throws. Preparing the Looper here is safe —
     * the frame callback posted by seedActiveActivity will never fire (Looper is never pumped),
     * but the synchronous ensureInspectionTag() call that precedes it is what actually seeds
     * the inspection slots, so tracking works correctly.
     */
    @get:Rule(order = 0)
    val looperRule: TestRule = TestRule { base, _ ->
        object : Statement() {
            override fun evaluate() {
                if (Looper.myLooper() == null) Looper.prepare()
                base.evaluate()
            }
        }
    }

    @get:Rule(order = 1)
    val rule = createRecompositionTrackingRule()

    private fun fakeLibrary(id: String, name: String = "Library $id") = Library(
        uniqueId = "com.example:$id",
        artifactVersion = "1.0.0",
        name = name,
        description = "Description for $name",
        website = "https://example.com/$id",
        developers = listOf(Developer(name = "Author", organisationUrl = null)),
        organization = null,
        scm = null,
        licenses = setOf(
            License(name = "Apache-2.0", url = null, spdxId = "Apache-2.0", licenseContent = null, hash = "apache")
        ),
    )

    private val baseLibraries = listOf(
        fakeLibrary("alpha"),
        fakeLibrary("beta"),
        fakeLibrary("gamma"),
    )

    // --- Traditional variant ---

    @Test
    fun librariesTraditional_stableWhenSameListReferenceReassigned() {
        var libraries by mutableStateOf(baseLibraries)
        rule.setContent {
            val style = LibraryDefaults.librariesStyle(colors = LibraryDefaults.defaultVariantColors())
            Libraries(
                libraries = libraries,
                style = style,
                variant = LibrariesVariant.Traditional,
                modifier = Modifier.fillMaxSize().testTag("list"),
            )
        }
        rule.waitForIdle()
        rule.resetRecompositionCounts()

        // Re-assign the exact same reference — structural equality, no change.
        libraries = baseLibraries
        rule.waitForIdle()

        rule.onNodeWithTag("list").assertStable()
    }

    @Test
    fun librariesTraditional_recomposesWhenListGrows() {
        var libraries by mutableStateOf(baseLibraries)
        rule.setContent {
            val style = LibraryDefaults.librariesStyle(colors = LibraryDefaults.defaultVariantColors())
            Libraries(
                libraries = libraries,
                style = style,
                variant = LibrariesVariant.Traditional,
                modifier = Modifier.fillMaxSize().testTag("list"),
            )
        }
        rule.waitForIdle()
        rule.resetRecompositionCounts()

        libraries = baseLibraries + fakeLibrary("delta")
        rule.waitForIdle()

        rule.onNodeWithTag("list").assertRecompositions(atLeast = 1)
    }

    // --- Refined variant ---

    @Test
    fun librariesRefined_stableWhenSameListReferenceReassigned() {
        var libraries by mutableStateOf(baseLibraries)
        rule.setContent {
            val style = LibraryDefaults.librariesStyle(colors = LibraryDefaults.defaultVariantColors())
            Libraries(
                libraries = libraries,
                style = style,
                variant = LibrariesVariant.Refined,
                modifier = Modifier.fillMaxSize().testTag("list"),
            )
        }
        rule.waitForIdle()
        rule.resetRecompositionCounts()

        libraries = baseLibraries
        rule.waitForIdle()

        rule.onNodeWithTag("list").assertStable()
    }

    @Test
    fun librariesRefined_recomposesWhenListGrows() {
        var libraries by mutableStateOf(baseLibraries)
        rule.setContent {
            val style = LibraryDefaults.librariesStyle(colors = LibraryDefaults.defaultVariantColors())
            Libraries(
                libraries = libraries,
                style = style,
                variant = LibrariesVariant.Refined,
                modifier = Modifier.fillMaxSize().testTag("list"),
            )
        }
        rule.waitForIdle()
        rule.resetRecompositionCounts()

        libraries = baseLibraries + fakeLibrary("delta")
        rule.waitForIdle()

        rule.onNodeWithTag("list").assertRecompositions(atLeast = 1)
    }
}
