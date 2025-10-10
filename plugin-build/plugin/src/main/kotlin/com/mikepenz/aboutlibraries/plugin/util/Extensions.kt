package com.mikepenz.aboutlibraries.plugin.util

import com.mikepenz.aboutlibraries.plugin.mapping.Library
import com.mikepenz.aboutlibraries.plugin.mapping.License
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import java.security.MessageDigest

internal fun String.toMD5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.toHex()
}

internal fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}

internal fun List<Library>.forLicense(license: License): List<Library> {
    return filter { it.licenses.contains(license.hash) || it.licenses.contains(license.internalHash) }
}

/**
 * Configures a task with the given name and type. If a task with the same name already exists, it will be configured instead of created.
 * Copyright: https://github.com/cashapp/licensee/blob/99b162fb4bdba838ff1ce805a213002dd6c02827/src/main/kotlin/app/cash/licensee/plugin.kt#L239
 */
internal fun <T : Task> TaskContainer.configure(name: String, type: Class<T>, config: (T) -> Unit): TaskProvider<T> = if (name in names) {
    named(name, type, config)
} else {
    register(name, type, config)
}