package com.mikepenz.aboutlibraries.ui.compose.android

import android.content.Context
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.util.withContext
import com.mikepenz.aboutlibraries.util.withJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * Creates a State<Libs?> that holds the [Libs] as loaded from Android using the [Context].
 * Warning: This API uses the name of the resource to find the libraries. Ensure R8 does not obfuscate the `aboutlibraries` RAW resource identifier.
 * Alternatively use the `produceLibraries(resId: Int)` API instead.
 *
 * @see Libs
 */
@Composable
fun produceLibraries(
    block: suspend (Context) -> Libs = { context ->
        Libs.Builder().withContext(context).build()
    },
): State<Libs?> {
    val context = LocalContext.current
    return produceState(initialValue = null) {
        value = withContext(Dispatchers.IO) {
            block(context)
        }
    }
}


/**
 * Creates a State<Libs?> that holds the [Libs] as loaded from Android using the [Context].
 *
 * @see Libs
 */
@Composable
fun produceLibraries(
    @RawRes
    resId: Int,
): State<Libs?> {
    val context = LocalContext.current
    return produceState(initialValue = null) {
        value = withContext(Dispatchers.IO) {
            Libs.Builder().withJson(context, resId).build()
        }
    }
}

// --- Deprecated wrappers to assist migration from rememberLibraries to produceLibraries ---
/**
 * Deprecated. Use produceLibraries(block: suspend (Context) -> Libs) instead.
 */
@Deprecated(
    message = "Use produceLibraries(block)",
    replaceWith = ReplaceWith("produceLibraries(block)"),
)
@Composable
fun rememberLibraries(block: suspend (Context) -> Libs): State<Libs?> = produceLibraries(block)

/**
 * Deprecated. Use produceLibraries(resId: Int) instead.
 */
@Deprecated(
    message = "Use produceLibraries(resId)",
    replaceWith = ReplaceWith("produceLibraries(resId)"),
)
@Composable
fun rememberLibraries(@RawRes resId: Int): State<Libs?> = produceLibraries(resId)

