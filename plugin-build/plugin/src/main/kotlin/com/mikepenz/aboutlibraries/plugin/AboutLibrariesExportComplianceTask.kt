package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension.Companion.PROP_EXPORT_ARTIFACT_GROUPS
import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension.Companion.PROP_EXPORT_PATH
import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension.Companion.PROP_PREFIX
import com.mikepenz.aboutlibraries.plugin.mapping.SpdxLicense
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.util.GradleVersion
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

abstract class AboutLibrariesExportComplianceTask : BaseAboutLibrariesTask() {

    @get:InputDirectory
    abstract val projectDirectory: DirectoryProperty

    override fun getDescription(): String = "Writes all libraries with their source and their license in CSV format to the configured directory"
    override fun getGroup(): String = "Help"

    @Input
    @Optional
    val exportPath: Provider<Directory> = project.providers.gradleProperty("${PROP_PREFIX}${PROP_EXPORT_PATH}")
        .map { path -> projectDirectory.get().dir(path) }
        .orElse(project.providers.gradleProperty(PROP_EXPORT_PATH).map { path ->
            projectDirectory.get().dir(path)
        })
        .orElse(
            if (GradleVersion.current() < GradleVersion.version("8.8")) {
                LOGGER.info("Fallback to non project isolated safe API for root directory.")
                // noinspection GradleProjectIsolation
                project.rootProject.layout.projectDirectory.dir(".")
            } else {
                @Suppress("UnstableApiUsage")
                project.isolated.rootProject.projectDirectory.dir(".")
            }
        )

    @Input
    val artifactGroups: Provider<String> = project.providers.gradleProperty("${PROP_PREFIX}${PROP_EXPORT_ARTIFACT_GROUPS}").orElse(
        project.providers.gradleProperty(PROP_EXPORT_ARTIFACT_GROUPS)
    ).orElse("")

    @Internal
    var neededLicenses = HashSet<SpdxLicense>()

    @Internal
    var librariesWithoutLicenses = HashSet<String>()

    private var unknownLicenses = HashMap<String, HashSet<String>>()

    @TaskAction
    fun action() {
        val result = createLibraryPostProcessor().process()
        val libraries = result.libraries
        val licenses = result.licenses

        val variant = variant.orNull
        if (variant != null) {
            println("")
            println("")
            println("Variant: $variant")
        }

        val groups = artifactGroups.orNull?.split(";") ?: emptyList()
        val exportTargetFolder = exportPath.get().asFile
        exportTargetFolder.mkdirs()

        val exportCsv = File(exportTargetFolder, "export.csv")
        exportCsv.delete()
        val exportTxt = File(exportTargetFolder, "export.txt")
        exportTxt.delete()
        val dependenciesFolder = File(exportTargetFolder, "dependencies")
        dependenciesFolder.deleteRecursively()

        val ungroupedKey = "zzzzz_ungrouped"
        val groupSorted = libraries.groupBy {
            for (group in groups) {
                if (it.artifactId.startsWith(group)) {
                    return@groupBy group
                }
            }
            return@groupBy ungroupedKey
        }

        exportTxt.appendText("LIBRARIES:\n")

        for (entry in groupSorted) {
            val ungrouped = entry.key == ungroupedKey
            val group = if (ungrouped) "ungrouped" else entry.key

            exportCsv.appendText("${group};;;\n")
            exportTxt.appendText("${group}\n")

            for (library in entry.value) {
                val fullLicenses = library.licenses.mapNotNull { licenses[it] }
                fullLicenses.map { it.spdxId ?: it.name }.forEach { licenseId ->
                    try {
                        neededLicenses.add(SpdxLicense.getById(licenseId))
                    } catch (_: Throwable) {
                        if (licenseId != "") {
                            val libsWithMissing = unknownLicenses.getOrDefault(licenseId, HashSet())
                            libsWithMissing.add(library.artifactId)
                            unknownLicenses[licenseId] = libsWithMissing
                        } else {
                            librariesWithoutLicenses.add(library.artifactId)
                        }
                    }
                }

                exportCsv.appendText("${library.name};${library.artifactId};${fullLicenses.joinToString(",") { it.spdxId ?: it.name }};${library.website}\n")
                exportTxt.appendText("${library.name};${library.artifactId};${fullLicenses.joinToString(",") { it.spdxId ?: it.name }}\n")

                var targetFolder = library.artifactId
                if (!ungrouped) {
                    targetFolder = "${group}/${library.artifactId}"
                }

                val libraryTargetFolder = File(dependenciesFolder, targetFolder)
                libraryTargetFolder.mkdirs()

                try {
                    val source = library.artifactFolder?.toPath()
                    if (source != null) {
                        Files.walk(source).forEach { s ->
                            val targetPath = libraryTargetFolder.toPath()
                            val fn = s.fileName.toString()
                            if (!Files.isDirectory(s) && (fn.endsWith(".aar") || fn.endsWith(".jar") || fn.endsWith(".pom")) && !fn.endsWith("-javadoc.jar")) {
                                Files.copy(s, targetPath.resolve(fn), StandardCopyOption.REPLACE_EXISTING)
                            }
                        }
                    }
                } catch (ex: Exception) {
                    LOGGER.error("Failed to copy library files for ${library.artifactId}", ex)
                }
            }
        }

        exportTxt.appendText("\n\nLICENSES:\n")
        for (license in neededLicenses) {
            exportTxt.appendText("${license.id};${license.fullName};${license.getUrl()}\n")
        }

        exportTxt.appendText("\n\nARTIFACTS WITHOUT LICENSE:\n")
        for (license in librariesWithoutLicenses) {
            exportTxt.appendText("${license}\n")
        }

        exportTxt.appendText("\n\nUNKNOWN LICENSES:\n")
        for (entry in unknownLicenses) {
            exportTxt.appendText("${entry.key}\n")
            exportTxt.appendText("-- ${entry.value}\n")
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AboutLibrariesExportComplianceTask::class.java)
    }
}