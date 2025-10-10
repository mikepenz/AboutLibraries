package com.mikepenz.aboutlibraries.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import com.mikepenz.aboutlibraries.Libs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Creates a State<Libs?> that holds the [Libs] as loaded by the [libraries].
 *
 * @see Libs
 */
@Composable
fun produceLibraries(
    libraries: ByteArray,
): State<Libs?> = produceLibraries {
    libraries.decodeToString()
}

/**
 * Creates a State<Libs?> that holds the [Libs] as loaded by the [block].
 *
 * @see Libs
 */
@Composable
fun produceLibraries(
    block: suspend () -> String,
): State<Libs?> {
    return produceState(initialValue = null) {
        value = withContext(Dispatchers.Default) {
            Libs.Builder()
                .withJson(block())
                .build()
        }
    }
}


/**
 * Creates a State<Libs?> that holds the [Libs] as loaded by the [libraries].
 *
 * @see Libs
 */
@Composable
fun produceLibraries(
    libraries: String,
): State<Libs?> {
    return produceState(initialValue = null) {
        value = withContext(Dispatchers.Default) {
            Libs.Builder().withJson(libraries).build()
        }
    }
}

// --- Deprecated wrappers to assist migration from rememberLibraries to produceLibraries ---
/**
 * Deprecated. Use produceLibraries(libraries: ByteArray) instead.
 */
@Deprecated(
    message = "Use produceLibraries(libraries: ByteArray)",
    replaceWith = ReplaceWith("produceLibraries(libraries)"),
)
@Composable
fun rememberLibraries(libraries: ByteArray): State<Libs?> = produceLibraries(libraries)

/**
 * Deprecated. Use produceLibraries(block: suspend () -> String) instead.
 */
@Deprecated(
    message = "Use produceLibraries(block: suspend () -> String)",
    replaceWith = ReplaceWith("produceLibraries(block)"),
)
@Composable
fun rememberLibraries(block: suspend () -> String): State<Libs?> = produceLibraries(block)

/**
 * Deprecated. Use produceLibraries(libraries: String) instead.
 */
@Deprecated(
    message = "Use produceLibraries(libraries: String)",
    replaceWith = ReplaceWith("produceLibraries(libraries)"),
)
@Composable
fun rememberLibraries(libraries: String): State<Libs?> = produceLibraries(libraries)