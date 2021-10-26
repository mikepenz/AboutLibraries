package com.mikepenz.aboutlibraries.plugin.util

import com.mikepenz.aboutlibraries.plugin.mapping.Library
import com.mikepenz.aboutlibraries.plugin.mapping.License
import com.mikepenz.aboutlibraries.plugin.model.CollectedContainer
import com.mikepenz.aboutlibraries.plugin.parser.m2.PomReader
import com.mikepenz.aboutlibraries.plugin.util.LicenseUtil.fetchRemoteLicense
import com.mikepenz.aboutlibraries.plugin.util.PomLoader.resolvePomFile
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.internal.artifacts.DefaultModuleVersionIdentifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.regex.Pattern

class AboutLibrariesProcessor(
    private val dependencyHandler: DependencyHandler,
    private val collectedDependencies: CollectedContainer,
    private val configFolder: File?,
    private val exclusionPatterns: List<Pattern>,
    private val fetchRemoteLicense: Boolean,
    private var variant: String? = null
) {

    private val handledLibraries = HashSet<String>()

    fun gatherDependencies(): ArrayList<Library> {
        if (fetchRemoteLicense) {
            LOGGER.debug("Will fetch remote licenses from repository.")
        }

        val collectedDependencies = collectedDependencies.dependenciesForVariant(variant)
        println("All dependencies.size=${collectedDependencies.size}")

        val librariesList = ArrayList<Library>()
        for (dependency in collectedDependencies) {
            val groupArtifact = dependency.key.split(":")
            val version = dependency.value.first()
            val versionIdentifier = DefaultModuleVersionIdentifier.newId(groupArtifact[0], groupArtifact[1], version)
            val file = dependencyHandler.resolvePomFile(groupArtifact[0], versionIdentifier, false)
            if (file != null) {
                try {
                    parseDependency(librariesList, file)
                } catch (ex: Throwable) {
                    LOGGER.error("--> Failed to write dependency information for: $groupArtifact", ex)
                }
            }
        }
        return librariesList
    }

    private fun parseDependency(libraries: MutableList<Library>, artifactFile: File) {
        var artifactPomText = artifactFile.readText().trim()
        if (artifactPomText[0] != '<') {
            LOGGER.warn("--> ${artifactFile.path} contains a invalid character at the first position. Applying workaround.")
            artifactPomText = artifactPomText.substring(artifactPomText.indexOf('<'))
        }

        val pomReader = PomReader(artifactFile.inputStream())
        val uniqueId = pomReader.groupId + ":" + pomReader.artifactId

        for (pattern in exclusionPatterns) {
            if (pattern.matcher(uniqueId).matches()) {
                println("--> Skipping ${uniqueId}, matching exclusion pattern")
                return
            }
        }

        LOGGER.debug(
            "--> ArtifactPom for [{}:{}]:\n{}\n\n",
            pomReader.groupId,
            pomReader.artifactId,
            artifactPomText
        )

        // check if we shall skip this specific uniqueId
        if (shouldSkip(uniqueId)) {
            return
        }

        // remember that we handled the library
        handledLibraries.add(uniqueId)

        // we also want to check if there are parent POMs with additional information
        var parentPomReader: PomReader? = null
        if (pomReader.hasParent()) {
            val parentPomFile = dependencyHandler.resolvePomFile(
                uniqueId,
                DefaultModuleVersionIdentifier.newId(pomReader.parentGroupId, pomReader.parentArtifactId, pomReader.parentVersion),
                true
            )
            if (parentPomFile != null) {
                val parentPomText = parentPomFile.readText()
                LOGGER.debug("--> ArtifactPom ParentPom for [{}:{}]:\n{}\n\n", pomReader.groupId, pomReader.artifactId, parentPomText)
                parentPomReader = PomReader(parentPomFile.inputStream())
            } else {
                LOGGER.warn(
                    "--> ArtifactPom reports ParentPom for [{}:{}] but couldn't resolve it",
                    pomReader.groupId,
                    pomReader.artifactId
                )
            }
        } else {
            LOGGER.debug("--> No Artifact Parent Pom found for [{}:{}]", pomReader.groupId, pomReader.artifactId)
        }

        // get the url for the author
        var libraryName = fixLibraryName(uniqueId, pomReader.name) // get name of the library
        val libraryDescription = fixLibraryDescription(uniqueId, pomReader.description.takeIf { it.isNotBlank() } ?: parentPomReader?.description ?: "")

        val artifactVersion = pomReader.version ?: parentPomReader?.version // get the version of the library
        if (artifactVersion.isNullOrBlank()) {
            println("----> Failed to identify version for: $uniqueId")
        }
        val libraryWebsite = pomReader.homePage ?: parentPomReader?.homePage // get the url to the library

        // the list of licenses a lib may have
        val licenses = (pomReader.licenses.takeIf { it.isNotEmpty() } ?: parentPomReader?.licenses)?.map {
            License(it.name, it.url, year = resolveLicenseYear(uniqueId, it.url))
        }?.toHashSet()
        if (licenses != null) {
            fetchRemoteLicense(uniqueId, pomReader.scm ?: parentPomReader?.scm, licenses)
        }

        if (libraryName?.isBlank() == true) {
            println("Could not get the name for ${uniqueId}, Using $uniqueId")
            libraryName = uniqueId
        }

        val library = Library(
            uniqueId,
            artifactVersion,
            libraryName,
            libraryDescription,
            libraryWebsite,
            pomReader.developers.takeIf { it.isNotEmpty() } ?: parentPomReader?.developers ?: emptyList(),
            pomReader.organization ?: parentPomReader?.organization,
            pomReader.scm ?: parentPomReader?.scm,
            licenses ?: emptySet(),
            artifactFile.parentFile?.parentFile // artifactFile references the pom directly
        )

        LOGGER.debug("Adding library: {}", library)
        libraries.add(library)
    }

    /**
     * Ensures and applies fixes to the library names (shorten, ...)
     */
    private fun fixLibraryName(uniqueId: String, value: String): String? {
        return if (value.startsWith("Android Support Library")) {
            value.replace("Android Support Library", "Support")
        } else if (value.startsWith("Android Support")) {
            value.replace("Android Support", "Support")
        } else if (value.startsWith("org.jetbrains.kotlin:")) {
            value.replace("org.jetbrains.kotlin:", "")
        } else {
            value
        }
    }

    /**
     * Ensures and applies fixes to the library descriptions (remove 'null', ...)
     */
    private fun fixLibraryDescription(uniqueId: String, value: String): String {
        return value.takeIf { it != "null" } ?: ""
    }

    private fun resolveLicenseYear(uniqueId: String, repositoryLink: String?): String? {
        return null
    }

    /**
     * Skip libraries which have a core dependency and we don't want it to show up more than necessary
     */
    private fun shouldSkip(uniqueId: String): Boolean {
        return handledLibraries.contains(uniqueId) || uniqueId == "com.mikepenz:aboutlibraries" || uniqueId == "com.mikepenz:aboutlibraries-definitions"
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(AboutLibrariesProcessor::class.java)
    }
}
