package com.mikepenz.aboutlibraries.plugin


import com.mikepenz.aboutlibraries.plugin.mapping.License
import com.mikepenz.aboutlibraries.plugin.mapping.SpdxLicense
import com.mikepenz.aboutlibraries.plugin.mapping.writeToDisk
import com.mikepenz.aboutlibraries.plugin.util.LibrariesProcessor
import com.mikepenz.aboutlibraries.plugin.util.toMD5
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory
import java.io.File

@CacheableTask
abstract class AboutLibrariesTask : BaseAboutLibrariesTask() {

    private val neededLicenses = HashSet<String>()

    @OutputDirectory
    lateinit var dependencies: File

    @Internal
    private lateinit var combinedLibrariesOutputFile: File
    private lateinit var outputValuesFolder: File
    private lateinit var outputRawFolder: File

    fun getCombinedLibrariesOutputFile(): File {
        return File(outputRawFolder, "aboutlibraries.json")
    }

    @OutputDirectory
    public fun getValuesFolder(): File {
        return File(dependencies, "values")
    }

    @OutputDirectory
    public fun getRawFolder(): File {
        return File(dependencies, "raw")
    }

    /**
     * Loos inside the *.jar and tries to find a license file to include in the apk
     */
    private fun tryToFindAndWriteLibrary(licenseId: String?): Boolean {
        licenseId ?: return false
        try {
            LOGGER.debug("--> Try load library with ID {}", licenseId)
            var successfulXml = false
            var resultFile = File(outputValuesFolder, "license_${licenseId}_strings.xml")
            resultFile.delete()
            javaClass.getResourceAsStream("/values/license_${licenseId}_strings.xml")?.use {
                resultFile.appendBytes(it.readBytes())
                successfulXml = true
            } ?: run {
                LOGGER.debug("--> File did not exist {}", javaClass.getResource("values/license_${licenseId}_strings.xml"))
            }

            resultFile = File(outputRawFolder, "license_${licenseId}.txt")
            resultFile.delete()
            javaClass.getResourceAsStream("/static/license_${licenseId}.txt")?.use {
                resultFile.appendBytes(it.readBytes())
            }

            return successfulXml
        } catch (ex: Exception) {
            println("--> License not available: $licenseId")
        }
        return false
    }

    /**
     * Creates the additional RAW files with remote licenses if available, otherwise inclues the license from the available definitions
     */
    private fun handleLicenses(license: License) {
        val rl = license.remoteLicense
        if (rl == null || rl.isBlank()) {
            val spdx = license.spdxId
            if (spdx != null) {
                neededLicenses.add(spdx) // remember the license we hit
            }
        } else {
            val resultFile = File(getRawFolder(), "license_${rl.toMD5()}.txt")
            if (!resultFile.exists()) {
                resultFile.appendText(rl)
            }
        }
    }

    /**
     * Copy in the needed licenses to the relevant folder
     */
    private fun processNeededLicenses() {
        // now copy over all licenses
        for (licenseId in neededLicenses) {
            try {
                val enumLicense = SpdxLicense.valueOf(licenseId)

                // try to find and write license by aboutLibsId
                if (!tryToFindAndWriteLibrary(enumLicense.aboutLibsId)) {
                    // try to find and write license by id
                    if (!tryToFindAndWriteLibrary(enumLicense.id)) {
                        // license was not available generate the url license template
                        // TODO val resultFile =  File(outputValuesFolder, "license_${licenseId.lowercase(Locale.US)}_strings.xml")
                        // TODO val printWriter =  PrintWriter( OutputStreamWriter (resultFile.outputStream(), StandardCharsets.UTF_8), true)
                        // TODO val licenseBuilder =  MarkupBuilder(printWriter)
                        // TODO licenseBuilder.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
                        // TODO licenseBuilder.doubleQuotes = true
                        // TODO licenseBuilder.resources {
                        // TODO     string name : "define_license_${licenseId}", translatable: 'false', ""
                        // TODO     string name : "license_${licenseId}_licenseName", translatable: 'false', "${enumLicense.fullName}"
                        // TODO     string name : "license_${licenseId}_licenseWebsite", translatable: 'false', "${enumLicense.getUrl()}"
                        // TODO }
                        // TODO printWriter.close()
                    }
                }
            } catch (ex: Exception) {
                try {
                    if (!tryToFindAndWriteLibrary(licenseId)) {
                        println("--> License not available: $licenseId")
                    }
                } catch (ex2: Exception) {
                    println("--> License not available: $licenseId")
                }
            }
        }
    }

    @TaskAction
    public fun action() {
        // ensure directories exist
        this.outputValuesFolder = getValuesFolder()
        this.outputRawFolder = getRawFolder()
        this.combinedLibrariesOutputFile = getCombinedLibrariesOutputFile()

        val collectedDependencies = readInCollectedDependencies()
        val processor = LibrariesProcessor(getDependencyHandler(), collectedDependencies, getConfigPath(), exclusionPatterns, fetchRemoteLicense, variant)
        val libraries = processor.gatherDependencies()

        if (includeAllLicenses) {
            // Include all licenses
            neededLicenses.addAll(SpdxLicense.values().map { it.id })
        } else {
            // Include additional licenses explicitly requested.
            getAdditionalLicenses().forEach { al ->
                val foundLicense = SpdxLicense.values().find { li ->
                    li.name.equals(al, true) || li.id.equals(al, true)
                }
                if (foundLicense != null) {
                    neededLicenses.add(foundLicense.name)
                }
            }
        }

        // write to disk
        libraries.writeToDisk(combinedLibrariesOutputFile)
        libraries.forEach { lib ->
            lib.licenses.forEach { lic ->
                handleLicenses(lic)
            }
        }

        processNeededLicenses()
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AboutLibrariesTask::class.java)
    }
}