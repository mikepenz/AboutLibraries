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
