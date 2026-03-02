package com.mikepenz.aboutlibraries.plugin.util.parser

import com.mikepenz.aboutlibraries.plugin.mapping.Funding
import groovy.json.JsonSlurper
import org.slf4j.LoggerFactory
import java.io.File

object FundingReader {
    fun readFunding(configDir: File): Map<String, Set<Funding>> {
        val fundingDir = File(configDir, FUNDING_DIR)
        val fundingFile = File(fundingDir, FUNDING_FILE)
        return if (fundingDir.exists() && fundingFile.exists()) {
            val allFunding = mutableMapOf<String, Set<Funding>>()
            try {
                val funding = fundingFile.inputStream().use { JsonSlurper().parse(it) as Map<String, List<Map<String, String>>> }
                funding.forEach { (uniqueId, value) ->
                    val fundingSet = mutableSetOf<Funding>()
                    value.forEach {
                        fundingSet.add(Funding(it["platform"] ?: "", it["url"] ?: ""))
                    }
                    allFunding[uniqueId] = fundingSet
                }
            } catch (t: Throwable) {
                LOGGER.warn("Could not read the funding file", t)
            }
            return allFunding
        } else {
            LOGGER.debug("No custom libraries provided")
            emptyMap()
        }
    }

    private val LOGGER = LoggerFactory.getLogger(FundingReader::class.java)!!
    internal const val FUNDING_DIR = "funding"
    internal const val FUNDING_FILE = "funding.json"
}