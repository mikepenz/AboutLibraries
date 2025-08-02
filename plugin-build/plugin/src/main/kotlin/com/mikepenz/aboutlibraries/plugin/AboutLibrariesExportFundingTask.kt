package com.mikepenz.aboutlibraries.plugin

import com.mikepenz.aboutlibraries.plugin.mapping.Funding
import com.mikepenz.aboutlibraries.plugin.util.parser.FundingReader.FUNDING_DIR
import com.mikepenz.aboutlibraries.plugin.util.parser.FundingReader.FUNDING_FILE
import groovy.json.JsonGenerator
import groovy.json.JsonOutput
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.nio.charset.StandardCharsets

@CacheableTask
abstract class AboutLibrariesExportFundingTask : BaseAboutLibrariesTask() {
    // Disable fetching remote licenses for this task, not applicable
    override val fetchRemoteLicense: Provider<Boolean> = project.provider { false }

    // Force fetch remote funding all the time
    override val fetchRemoteFunding: Provider<Boolean> = project.provider { true }

    override fun getDescription(): String = "Exports the funding options for all used dependencies to the config folder"
    override fun getGroup(): String = "Help"

    @TaskAction
    fun action() {
        val configDirectory = configPath.orNull ?: throw IllegalArgumentException("The `configPath` has to be configured for this task to work.")
        val result = createLibraryPostProcessor().process()
        val libraries = result.libraries

        val outputFundingFile = configDirectory.dir(FUNDING_DIR).file(FUNDING_FILE).asFile
        val allFunding = mutableMapOf<String, Set<Funding>>()
        libraries.forEach {
            if (it.funding.isNotEmpty()) {
                allFunding[it.uniqueId] = it.funding
            }
        }
        val sortedFunding = allFunding.toSortedMap()

        val jsonGenerator = JsonGenerator.Options()
            .excludeNulls()
            .build()

        PrintWriter(OutputStreamWriter(outputFundingFile.outputStream(), StandardCharsets.UTF_8), true).use {
            it.write(jsonGenerator.toJson(sortedFunding).let { json -> if (prettyPrint.get()) JsonOutput.prettyPrint(json) else json })
        }
    }
}