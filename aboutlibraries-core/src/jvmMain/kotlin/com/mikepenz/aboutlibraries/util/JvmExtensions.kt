package com.mikepenz.aboutlibraries.util

import com.mikepenz.aboutlibraries.Libs

/**
 * Attach the generated library definition data as [ByteArray]
 */
fun Libs.Builder.withJson(byteArray: ByteArray): Libs.Builder {
    return withJson(byteArray.toString(Charsets.UTF_8))
}
