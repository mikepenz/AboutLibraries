package com.mikepenz.aboutlibraries.plugin.util

import com.mikepenz.aboutlibraries.plugin.mapping.Library
import com.mikepenz.aboutlibraries.plugin.mapping.License
import org.gradle.api.artifacts.ModuleIdentifier
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.api.artifacts.ResolvedModuleVersion
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import java.io.File
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

/**
 * Convenient helper to wrap a [ResolvedDependency] into a []ResolvedArtifact]
 * Required to handle `platform` dependencies.
 */
internal fun ResolvedDependency.toResolvedBomArtifact() = object : ResolvedArtifact {
    override fun getFile(): File? = null
    override fun getModuleVersion(): ResolvedModuleVersion = module
    override fun getName(): String = this@toResolvedBomArtifact.moduleName
    override fun getType(): String = "platform"
    override fun getExtension(): String = "pom"
    override fun getClassifier(): String? = null
    override fun getId() = object : ComponentArtifactIdentifier {
        override fun getComponentIdentifier() = object : ModuleComponentIdentifier {
            override fun getDisplayName(): String = this@toResolvedBomArtifact.module.id.toString()
            override fun getGroup(): String = this@toResolvedBomArtifact.module.id.module.group
            override fun getModule(): String = this@toResolvedBomArtifact.module.id.module.name
            override fun getVersion(): String = this@toResolvedBomArtifact.module.id.version
            override fun getModuleIdentifier(): ModuleIdentifier = this@toResolvedBomArtifact.module.id.module
        }

        override fun getDisplayName(): String = module.id.toString()
        override fun toString(): String = displayName
    }

    override fun toString(): String = id.displayName
}