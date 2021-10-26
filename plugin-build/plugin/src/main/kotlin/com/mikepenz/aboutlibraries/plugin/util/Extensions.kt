package com.mikepenz.aboutlibraries.plugin.util

import java.security.MessageDigest

internal fun String.toMD5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.toHex()
}

internal fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}