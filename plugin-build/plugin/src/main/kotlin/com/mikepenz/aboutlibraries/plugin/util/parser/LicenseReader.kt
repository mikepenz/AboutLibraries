package com.mikepenz.aboutlibraries.plugin.util.parser

import com.mikepenz.aboutlibraries.plugin.mapping.License
import groovy.json.JsonSlurper
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream

object LicenseReader {

    fun readLicenses(configDir: File): List<License> {
        val licensesDir = File(configDir, LICENSES_DIR)
        return if (licensesDir.exists()) {
            licensesDir.listFiles()?.mapNotNull {
                readLicense(it.name, it.inputStream())
            } ?: emptyList()
        } else {
            LOGGER.debug("No custom licenses provided")
            emptyList()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun readLicense(name: String, content: InputStream): License? {
        return try {
            val c = JsonSlurper().parse(content) as Map<String, String>
            License(
                c["name"]!!,
                c["url"],
                c["year"],
                c["content"]
            ).also {
                it.spdxId = c["spdxId"]
                it.internalHash = c["hash"]
            }
        } catch (t: Throwable) {
            LOGGER.error("Could not read the license ($name)", t)
            null
        }
    }

    /**
    "content": "content",
    "hash": "490e60979da91074741c32376710794f",
    "url": "https://spdx.org/licenses/Apache-2.0.txt",
    "spdxId": "Apache_2_0",
    "name": "Apache License 2.0"
     */

    private val LOGGER = LoggerFactory.getLogger(LicenseReader::class.java)!!
    private val LICENSES_DIR = "licenses"
}