package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.mapping.Library
import com.mikepenz.aboutlibraries.plugin.mapping.License
import groovy.json.StreamingJsonBuilder
import groovy.xml.MarkupBuilder
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.charset.StandardCharsets

@CacheableTask
abstract class AboutLibrariesTask extends BaseAboutLibrariesTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(AboutLibrariesTask.class);

    @Internal
    Set<String> neededLicenses = new HashSet<String>()

    private String variant
    private File dependencies

    @Internal
    private File combinedLibrariesOutputFile
    private File outputValuesFolder
    private File outputRawFolder

    File getCombinedLibrariesOutputFile() {
        if (asStringResource) {
            return new File(outputValuesFolder, "aboutlibraries.xml")
        } else {
            return new File(outputRawFolder, "aboutlibraries.json")
        }
    }

    @OutputDirectory
    public File getValuesFolder() {
        return new File(dependencies, "values")
    }

    @OutputDirectory
    public File getRawFolder() {
        return new File(dependencies, "raw")
    }

    @OutputDirectory
    public File getDependencies() {
        return dependencies
    }

    public void setDependencies(File dependencies) {
        this.dependencies = dependencies
    }

    public void setVariant(String variant) {
        this.variant = variant
    }

    /**
     * Loos inside the *.jar and tries to find a license file to include in the apk
     */
    def tryToFindAndWriteLibrary(def licenseId) {
        try {
            LOGGER.debug("--> Try load library with ID {}", licenseId)
            def successfulXml = false
            def resultFile = new File(outputValuesFolder, "license_${licenseId}_strings.xml")
            resultFile.delete()
            def is = getClass().getResourceAsStream("/values/license_${licenseId}_strings.xml")
            if (is != null) {
                resultFile.append(is)
                is.close()
                successfulXml = true
            } else {
                LOGGER.debug("--> File did not exist {}", getClass().getResource("values/license_${licenseId}_strings.xml"))
            }

            resultFile = new File(outputRawFolder, "license_${licenseId}.txt")
            resultFile.delete()
            is = getClass().getResourceAsStream("/static/license_${licenseId}.txt")
            if (is != null) {
                resultFile.append(is)
                is.close()
            }

            return successfulXml
        } catch (Exception ex) {
            println("--> License not available: ${licenseId}")
        }
        return false
    }

    /**
     * Creates the additional RAW files with remote licenses if available, otherwise inclues the license from the available definitions
     */
    def handleLicenses(Library library) {
        if (library.remoteLicense == null || library.remoteLicense.isBlank()) {
            if (!library.licenseIds.isEmpty()) {
                library.licenseIds.each {
                    neededLicenses.add(it) // remember the license we hit
                }
            }
        } else {
            def resultFile = new File(getRawFolder(), "license_${library.remoteLicense.md5()}.txt")
            if (!resultFile.exists()) {
                resultFile.append(library.remoteLicense, "UTF-8")
            }
        }
    }

    /**
     * Copy in the needed licenses to the relevant folder
     */
    def processNeededLicenses() {
        // now copy over all licenses
        for (String licenseId : neededLicenses) {
            try {
                def enumLicense = License.valueOf(licenseId)

                // try to find and write license by aboutLibsId
                if (!tryToFindAndWriteLibrary(enumLicense.aboutLibsId)) {
                    // try to find and write license by id
                    if (!tryToFindAndWriteLibrary(enumLicense.id)) {
                        // license was not available generate the url license template
                        def resultFile = new File(outputValuesFolder, "license_${licenseId.toLowerCase(Locale.US)}_strings.xml")
                        def printWriter = new PrintWriter(new OutputStreamWriter(resultFile.newOutputStream(), StandardCharsets.UTF_8), true)
                        def licenseBuilder = new MarkupBuilder(printWriter)
                        licenseBuilder.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
                        licenseBuilder.doubleQuotes = true
                        licenseBuilder.resources {
                            string name: "define_license_${licenseId}", translatable: 'false', ""
                            string name: "license_${licenseId}_licenseName", translatable: 'false', "${enumLicense.fullName}"
                            string name: "license_${licenseId}_licenseWebsite", translatable: 'false', "${enumLicense.getUrl()}"
                        }
                        printWriter.close()
                    }
                }
            } catch (Exception ex) {
                try {
                    if (!tryToFindAndWriteLibrary(licenseId)) {
                        println("--> License not available: ${licenseId}")
                    }
                } catch (Exception ex2) {
                    println("--> License not available: ${licenseId}")
                }
            }
        }
    }

    /**
     * Writes out the given library to disk
     */
    def writeDependency(MarkupBuilder resources, Library library) {
        def delimiter = ""
        def customProperties = ""
        if (isNotEmpty(library.libraryOwner)) {
            customProperties = customProperties + delimiter + "owner"
            delimiter = ";"
        }
        if (isNotEmpty(library.licenseYear)) {
            customProperties = customProperties + delimiter + "year"
            delimiter = ";"
        }

        resources.string name: "define_plu_${library.uniqueId}", translatable: 'false', "${customProperties}"
        if (isNotEmpty(library.author)) {
            resources.string name: "library_${library.uniqueId}_author", translatable: 'false', "${library.author}"
        }
        if (isNotEmpty(library.authorWebsite)) {
            resources.string name: "library_${library.uniqueId}_authorWebsite", translatable: 'false', "${library.authorWebsite}"
        }
        resources.string name: "library_${library.uniqueId}_libraryName", translatable: 'false', "${library.libraryName}"
        resources.string(name: "library_${library.uniqueId}_libraryDescription", translatable: 'false') {
            mkp.yieldUnescaped("<![CDATA[${library.libraryDescription}]]>")
        }
        resources.string name: "library_${library.uniqueId}_libraryVersion", translatable: 'false', "${library.libraryVersion}"
        resources.string name: "library_${library.uniqueId}_libraryArtifactId", translatable: 'false', "${library.artifactId}"
        // the maven artifactId
        if (isNotEmpty(library.libraryWebsite)) {
            resources.string name: "library_${library.uniqueId}_libraryWebsite", translatable: 'false', "${library.libraryWebsite}"
        }
        if (!library.licenseIds.isEmpty()) {
            resources.string name: "library_${library.uniqueId}_licenseIds", translatable: 'false', "${library.licenseIds.join(",")}"

            // note only for backwards compatibility. remove with v9.x.y
            resources.string name: "library_${library.uniqueId}_licenseId", translatable: 'false', "${library.licenseIds.first()}"
        }
        if (library.remoteLicense != null && !library.remoteLicense.isBlank()) {
            resources.string name: "library_${library.uniqueId}_remoteLicense", translatable: 'false', "${library.remoteLicense.md5()}"
        }
        if (library.isOpenSource) {
            resources.string name: "library_${library.uniqueId}_isOpenSource", translatable: 'false', "${library.isOpenSource}"
        }
        if (isNotEmpty(library.repositoryLink)) {
            resources.string name: "library_${library.uniqueId}_repositoryLink", translatable: 'false', "${library.repositoryLink}"
        }
        if (isNotEmpty(library.libraryOwner)) {
            resources.string name: "library_${library.uniqueId}_owner", translatable: 'false', "${library.libraryOwner}"
        }
        if (isNotEmpty(library.licenseYear)) {
            resources.string name: "library_${library.uniqueId}_year", translatable: 'false', "${library.licenseYear}"
        }
    }

    /**
     * Checks if the given string is empty.
     * Returns true if it is NOT empty
     */
    static def isNotEmpty(String value) {
        return value != null && value != ""
    }

    @TaskAction
    public void action() throws IOException {
        // ensure directories exist
        this.outputValuesFolder = getValuesFolder()
        this.outputRawFolder = getRawFolder()
        this.combinedLibrariesOutputFile = getCombinedLibrariesOutputFile()

        final def collectedDependencies = readInCollectedDependencies()
        final def processor = new AboutLibrariesProcessor(getDependencyHandler(), collectedDependencies, configPath, exclusionPatterns, fetchRemoteLicense, includeAllLicenses, additionalLicenses, variant)
        final def libraries = processor.gatherDependencies()

        if (processor.includeAllLicenses) {
            // Include all licenses
            neededLicenses.addAll(License.values())
        } else {
            // Include additional licenses explicitly requested.
            processor.additionalLicenses.each { final al ->
                final def foundLicense = License.values().find { final li ->
                    li.name().equalsIgnoreCase(al) || li.id.equalsIgnoreCase(al)
                }
                if (foundLicense != null) {
                    neededLicenses.add(foundLicense.name())
                }
            }
        }

        if (asStringResource) {
            def printWriter = new PrintWriter(new OutputStreamWriter(combinedLibrariesOutputFile.newOutputStream(), StandardCharsets.UTF_8), true)
            def combinedLibrariesBuilder = new MarkupBuilder(printWriter)
            combinedLibrariesBuilder.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
            combinedLibrariesBuilder.doubleQuotes = true
            combinedLibrariesBuilder.resources {
                for (final library in libraries) {
                    writeDependency(combinedLibrariesBuilder, library)
                    handleLicenses(library)
                }
                string name: "config_aboutLibraries_plugin", translatable: 'false', "yes"
            }
            printWriter.close()
        } else {
            def printWriter = new PrintWriter(new OutputStreamWriter(combinedLibrariesOutputFile.newOutputStream(), StandardCharsets.UTF_8), true)
            StreamingJsonBuilder builder = new StreamingJsonBuilder(printWriter)

            builder(libraries) { Library library ->
                uniqueId library.uniqueId
                artifactId library.artifactId
                author library.author
                authorWebsite library.authorWebsite
                libraryName library.libraryName
                libraryDescription library.libraryDescription
                libraryVersion library.libraryVersion
                libraryWebsite library.libraryWebsite
                isOpenSource library.isOpenSource()
                repositoryLink library.repositoryLink
                libraryOwner library.libraryOwner
                licenseIds library.licenseIds
                licenseYear library.licenseYear
                remoteLicense library.remoteLicense?.md5()
            }

            printWriter.close()

            libraries.forEach {
                handleLicenses(it)
            }
        }

        processNeededLicenses()
    }
}