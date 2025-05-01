package com.mikepenz.aboutlibraries.plugin.util.parser

import com.mikepenz.aboutlibraries.plugin.mapping.Developer
import com.mikepenz.aboutlibraries.plugin.mapping.Funding
import com.mikepenz.aboutlibraries.plugin.mapping.Library
import com.mikepenz.aboutlibraries.plugin.mapping.Organization
import com.mikepenz.aboutlibraries.plugin.mapping.Scm
import groovy.json.JsonSlurper
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream

object LibraryReader {

    fun readLibraries(configDir: File): List<Library> {
        val librariesDir = File(configDir, LIBRARIES_DIR)
        return if (librariesDir.exists()) {
            librariesDir.listFiles()?.mapNotNull {
                readLibrary(it.name, it.inputStream())
            } ?: emptyList()
        } else {
            LOGGER.debug("No custom libraries provided")
            emptyList()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun readLibrary(name: String, content: InputStream): Library? {
        return try {
            val c = JsonSlurper().parse(content) as Map<String, *>

            val developers = mutableListOf<Developer>()
            (c["developers"] as? List<Map<String, String>>)?.forEach {
                developers.add(Developer(it["name"], it["organisationUrl"]))
            }

            val organization = (c["organization"] as? Map<String, String>)?.let {
                Organization(it["name"] ?: "", it["url"])
            }

            val scm = (c["scm"] as? Map<String, String>)?.let {
                Scm(it["connection"], it["developerConnection"], it["url"])
            }

            val licenses = (c["licenses"] as? List<String>)?.toSet() ?: emptySet()

            val funding = mutableSetOf<Funding>()
            (c["funding"] as? List<Map<String, String>>)?.forEach {
                funding.add(Funding(it["platform"] ?: "", it["url"] ?: ""))
            }

            Library(
                c["uniqueId"] as String,
                c["artifactVersion"] as? String,
                c["name"] as? String,
                c["description"] as? String,
                c["website"] as? String,
                developers,
                organization,
                scm,
                licenses,
                funding,
                c["tag"] as? String
            )
        } catch (t: Throwable) {
            LOGGER.error("Could not read the license ($name)", t)
            null
        }
    }

    private val LOGGER = LoggerFactory.getLogger(LibraryReader::class.java)!!
    private const val LIBRARIES_DIR = "libraries"
}