package com.mikepenz.aboutlibraries.ui

import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.mikepenz.aboutlibraries.ui.compose.variant.DefaultLibraryBadges
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrariesDensity
import com.mikepenz.aboutlibraries.ui.compose.variant.refined.RefinedRow
import com.mikepenz.aboutlibraries.ui.compose.variant.traditional.TraditionalRow
import dejavu.assertRecompositions
import dejavu.assertStable
import dejavu.createRecompositionTrackingRule
import dejavu.resetRecompositionCounts
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.junit.runner.RunWith

/**
 * Verifies that library row composables don't recompose unnecessarily when inputs are unchanged,
 * and recompose exactly once when a relevant input changes.
 */
@RunWith(AndroidJUnit4::class)
class LibraryRowStabilityTest {

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

    // --- fixtures ---

    private fun fakeLibrary(
        id: String = "com.example:lib",
        name: String = "Example Library",
        version: String = "1.0.0",
        author: String = "Example Author",
        licenseId: String = "Apache-2.0",
    ) = Library(
        uniqueId = id,
        artifactVersion = version,
        name = name,
        description = "A sample library for testing recomposition stability.",
        website = "https://example.com",
        developers = listOf(Developer(name = author, organisationUrl = null)),
        organization = null,
        scm = null,
        licenses = setOf(
            License(name = licenseId, url = null, spdxId = licenseId, licenseContent = null, hash = licenseId)
        ),
    )

    // --- TraditionalRow ---

    @Test
    fun traditionalRow_stableWhenInputsUnchanged() {
        val library = fakeLibrary()
        rule.setContent {
            val style = LibraryDefaults.librariesStyle(colors = LibraryDefaults.defaultVariantColors())
            TraditionalRow(
                library = library,
                expanded = false,
                onToggle = {},
                density = LibrariesDensity.Cozy,
                badges = DefaultLibraryBadges,
                style = style,
                modifier = Modifier.testTag("row"),
            )
        }
        rule.waitForIdle()
        rule.resetRecompositionCounts()
        rule.mainClock.advanceTimeByFrame()

        rule.onNodeWithTag("row").assertStable()
    }

    @Test
    fun traditionalRow_recomposesExactlyOnceWhenExpandedChanges() {
        val library = fakeLibrary()
        var expanded by mutableStateOf(false)
        rule.setContent {
            val style = LibraryDefaults.librariesStyle(colors = LibraryDefaults.defaultVariantColors())
            TraditionalRow(
                library = library,
                expanded = expanded,
                onToggle = {},
                density = LibrariesDensity.Cozy,
                badges = DefaultLibraryBadges,
                style = style,
                modifier = Modifier.testTag("row"),
            )
        }
        rule.waitForIdle()
        rule.resetRecompositionCounts()

        expanded = true
        rule.waitForIdle()

        rule.onNodeWithTag("row").assertRecompositions(exactly = 1)
    }

    @Test
    fun traditionalRow_stableWhenUnrelatedStateChanges() {
        val library = fakeLibrary()
        var unrelated by mutableIntStateOf(0)
        rule.setContent {
            @Suppress("UNUSED_EXPRESSION") unrelated
            val style = LibraryDefaults.librariesStyle(colors = LibraryDefaults.defaultVariantColors())
            TraditionalRow(
                library = library,
                expanded = false,
                onToggle = {},
                density = LibrariesDensity.Cozy,
                badges = DefaultLibraryBadges,
                style = style,
                modifier = Modifier.testTag("row"),
            )
        }
        rule.waitForIdle()
        rule.resetRecompositionCounts()

        unrelated++
        rule.waitForIdle()

        rule.onNodeWithTag("row").assertStable()
    }

    // --- RefinedRow ---

    @Test
    fun refinedRow_stableWhenInputsUnchanged() {
        val library = fakeLibrary()
        rule.setContent {
            val style = LibraryDefaults.librariesStyle(colors = LibraryDefaults.defaultVariantColors())
            RefinedRow(
                library = library,
                expanded = false,
                onToggle = {},
                density = LibrariesDensity.Cozy,
                badges = DefaultLibraryBadges,
                style = style,
                modifier = Modifier.testTag("row"),
            )
        }
        rule.waitForIdle()
        rule.resetRecompositionCounts()
        rule.mainClock.advanceTimeByFrame()

        rule.onNodeWithTag("row").assertStable()
    }

    @Test
    fun refinedRow_recomposesExactlyOnceWhenExpandedChanges() {
        val library = fakeLibrary()
        var expanded by mutableStateOf(false)
        rule.setContent {
            val style = LibraryDefaults.librariesStyle(colors = LibraryDefaults.defaultVariantColors())
            RefinedRow(
                library = library,
                expanded = expanded,
                onToggle = {},
                density = LibrariesDensity.Cozy,
                badges = DefaultLibraryBadges,
                style = style,
                modifier = Modifier.testTag("row"),
            )
        }
        rule.waitForIdle()
        rule.resetRecompositionCounts()

        expanded = true
        rule.waitForIdle()

        rule.onNodeWithTag("row").assertRecompositions(exactly = 1)
    }

    @Test
    fun refinedRow_stableWhenUnrelatedStateChanges() {
        val library = fakeLibrary()
        var unrelated by mutableIntStateOf(0)
        rule.setContent {
            @Suppress("UNUSED_EXPRESSION") unrelated
            val style = LibraryDefaults.librariesStyle(colors = LibraryDefaults.defaultVariantColors())
            RefinedRow(
                library = library,
                expanded = false,
                onToggle = {},
                density = LibrariesDensity.Cozy,
                badges = DefaultLibraryBadges,
                style = style,
                modifier = Modifier.testTag("row"),
            )
        }
        rule.waitForIdle()
        rule.resetRecompositionCounts()

        unrelated++
        rule.waitForIdle()

        rule.onNodeWithTag("row").assertStable()
    }
}
