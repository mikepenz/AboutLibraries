package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.mapping.License
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files
import java.nio.file.StandardCopyOption

public class AboutLibrariesExportComplianceTask extends BaseAboutLibrariesTask {

    private String variant = null
    private Set<License> neededLicenses = new HashSet<License>()
    private Set<String> librariesWithoutLicenses = new HashSet<String>()
    private HashMap<String, HashSet<String>> unknownLicenses = new HashMap<String, HashSet<String>>()

    public void setVariant(String variant) {
        this.variant = variant
    }

    @Internal
    String getVariant() {
        return variant
    }

    @Internal
    Set<License> getNeededLicenses() {
        return neededLicenses
    }

    @Internal
    Set<String> getLibrariesWithoutLicenses() {
        return librariesWithoutLicenses
    }

    @Internal
    HashMap<String, HashSet<String>> getUnknownLicenses() {
        return unknownLicenses
    }

    def gatherDependencies(def project) {
        final def exportPath = project.hasProperty("exportPath") ? project.getProperty("exportPath") : project.rootDir.absolutePath
        final def artifactGroups = project.hasProperty("artifactGroups") ? project.getProperty("artifactGroups") : ""

        if (exportPath == null) {
            throw new IllegalArgumentException("Please specify `exportPath` via the gradle CLI (-PexportPath=...)")
        }

        final def libraries = new AboutLibrariesProcessor().gatherDependencies(project, configPath, exclusionPatterns, includeAllLicenses, additionalLicenses, variant)

        if (variant != null) {
            println ""
            println ""
            println "Variant: ${variant}"
        }

        final def groups = artifactGroups?.split(";")
        final def exportTargetFolder = new File(exportPath)
        exportTargetFolder.mkdirs()

        final def exportCsv = new File(exportTargetFolder, "export.csv")
        exportCsv.delete()
        final def exportTxt = new File(exportTargetFolder, "export.txt")
        exportTxt.delete()

        final def dependenciesFolder = new File(exportTargetFolder, "dependencies")
        dependenciesFolder.deleteDir()

        def ungroupedKey = "zzzzz_ungrouped"
        def groupSorted = libraries.groupBy {
            for (final group in groups) {
                if (it.artifactId.startsWith(group)) {
                    return group
                }
            }
            return ungroupedKey
        }

        exportTxt.append("LIBRARIES:\n")

        for (entry in groupSorted) {
            def ungrouped = entry.key == ungroupedKey
            def group = ungrouped ? "ungrouped" : entry.key

            exportCsv.append("${group};;;\n")
            exportTxt.append("${group}\n")

            for (final library in entry.value) {
                library.licenseIds.each { licenseId ->
                    try {
                        neededLicenses.add(License.valueOf(licenseId))
                    } catch (Exception ex) {
                        if (licenseId != null && licenseId != "") {
                            HashSet<String> libsWithMissing = unknownLicenses.getOrDefault(licenseId, new HashSet<String>())
                            libsWithMissing.add(library.artifactId)
                            unknownLicenses.put(licenseId, libsWithMissing)
                        } else {
                            librariesWithoutLicenses.add(library.artifactId)
                        }
                    }
                }

                exportCsv.append("${library.libraryName};${library.artifactId};${library.licenseIds};${library.libraryWebsite ?: library.authorWebsite}\n")
                exportTxt.append("${library.libraryName};${library.artifactId};${library.licenseIds}\n")

                def targetFolder = "${library.artifactId}"
                if (!ungrouped) {
                    targetFolder = "${group}/${library.artifactId}"
                }

                def libraryTargetFolder = new File(dependenciesFolder, targetFolder)
                libraryTargetFolder.mkdirs()

                try {
                    def source = library.artifactFolder.toPath()
                    Files.walk(source).forEach { final s ->
                        final def targetPath = libraryTargetFolder.toPath()
                        final def fn = s.fileName.toString()
                        if (!Files.isDirectory(s) && (fn.endsWith(".aar") || fn.endsWith(".jar") || fn.endsWith(".pom")) && !fn.endsWith("-javadoc.jar")) {
                            Files.copy(s, targetPath.resolve(fn), StandardCopyOption.REPLACE_EXISTING)
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace()
                }
            }
        }

        exportTxt.append("\n\nLICENSES:\n")
        for (final license in neededLicenses) {
            exportTxt.append("${license.id};${license.fullName};${license.url}\n")
        }

        exportTxt.append("\n\nARTIFACTS WITHOUT LICENSE:\n")
        for (final license in librariesWithoutLicenses) {
            exportTxt.append("${license}\n")
        }

        exportTxt.append("\n\nUNKNOWN LICENSES:\n")
        for (final entry in unknownLicenses) {
            exportTxt.append("${entry.key}\n")
            exportTxt.append("-- ${entry.value}\n")
        }


    }

    @TaskAction
    public void action() throws IOException {
        gatherDependencies(project)
    }
}