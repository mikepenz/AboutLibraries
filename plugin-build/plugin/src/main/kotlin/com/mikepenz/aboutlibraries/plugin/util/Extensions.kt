package com.mikepenz.aboutlibraries.plugin.util

import com.mikepenz.aboutlibraries.plugin.mapping.Library
import com.mikepenz.aboutlibraries.plugin.mapping.License
import java.security.MessageDigest

internal fun String.toMD5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.toHex()
}

internal fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}

internal fun List<Library>.forLicense(license: License): List<Library> {
    return filter { it.licenses.contains(license.hash) }
}