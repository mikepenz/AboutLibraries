package com.mikepenz.aboutlibraries.util

import com.mikepenz.aboutlibraries.Libs

fun Libs.Builder.withJson(byteArray: ByteArray): Libs.Builder {
    return withJson(byteArray.toString(Charsets.UTF_8))
}
