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

internal fun <T> chooseValue(uniqueId: String, key: String, value: T?, block: () -> T?): T? {
    return value ?: block.invoke()?.also {
        LibrariesProcessor.LOGGER.info("----> Had to fallback to parent '$key' for '$uniqueId' -- result: $it")
    }
}

internal fun chooseValue(uniqueId: String, key: String, value: String?, block: () -> String?): String? {
    return if (value.isNullOrBlank()) {
        block.invoke()?.also {
            LibrariesProcessor.LOGGER.info("----> Had to fallback to parent '$key' for '$uniqueId' -- result: $it")
        }
    } else {
        value
    }
}

internal fun <T> chooseValue(uniqueId: String, key: String, value: Array<T>?, block: () -> Array<T>?): Array<T>? {
    return if (value.isNullOrEmpty()) {
        block.invoke()?.also {
            LibrariesProcessor.LOGGER.info("----> Had to fallback to parent '$key' for '$uniqueId'")
        }
    } else {
        value
    }
}