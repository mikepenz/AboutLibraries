package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.mapping.Library
import com.mikepenz.aboutlibraries.plugin.mapping.License
import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.charset.StandardCharsets

@CacheableTask
public class AboutLibrariesTask extends DefaultTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(AboutLibrariesTask.class);

    @Internal
    Set<String> neededLicenses = new HashSet<String>()

    private File dependencies
    @Internal
    private File combinedLibrariesOutputFile
    private File outputValuesFolder
    private File outputRawFolder

    File getCombinedLibrariesOutputFile() {
        return new File(outputValuesFolder, "aboutlibraries.xml")
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

    @InputFiles
    public void setDependencies(File dependencies) {
        this.dependencies = dependencies
    }

    def gatherDependencies(def project) {
        // ensure directories exist
        this.outputValuesFolder = getValuesFolder()
        this.outputRawFolder = getRawFolder()
        this.combinedLibrariesOutputFile = getCombinedLibrariesOutputFile()

        def libraries = new AboutLibrariesProcessor().gatherDependencies(project)
        def printWriter = new PrintWriter(new OutputStreamWriter(combinedLibrariesOutputFile.newOutputStream(), StandardCharsets.UTF_8), true)
        def combinedLibrariesBuilder = new MarkupBuilder(printWriter)
        combinedLibrariesBuilder.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
        combinedLibrariesBuilder.doubleQuotes = true
        combinedLibrariesBuilder.resources {
            for (final library in libraries) {
                writeDependency(combinedLibrariesBuilder, library)

                if (isNotEmpty(library.licenseId)) {
                    neededLicenses.add(library.licenseId) // remember the license we hit
                }
            }
            string name: "config_aboutLibraries_plugin", "yes"
        }
        printWriter.close()

        processNeededLicenses()
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
                        def resultFile = new File(outputValuesFolder, "license_${licenseId.toLowerCase()}_strings.xml")
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
        if (isNotEmpty(library.licenseId)) {
            resources.string name: "library_${library.uniqueId}_licenseId", translatable: 'false', "${library.licenseId}"
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
        gatherDependencies(project)
    }
}
