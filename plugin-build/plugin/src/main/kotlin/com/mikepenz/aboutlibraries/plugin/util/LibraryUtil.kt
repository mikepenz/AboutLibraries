package com.mikepenz.aboutlibraries.plugin.util

import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import com.mikepenz.aboutlibraries.plugin.mapping.Developer
import com.mikepenz.aboutlibraries.plugin.mapping.Funding
import com.mikepenz.aboutlibraries.plugin.mapping.Library

fun List<Library>.processDuplicates(
    duplicateMode: DuplicateMode,
    duplicateRule: DuplicateRule,
): List<Library> {
    fun mappedLibs(): Map<String, List<Library>> {
        return this.groupBy {
            when (duplicateRule) {
                DuplicateRule.GROUP -> it.groupId + it.licenses.joinToString(",")
                DuplicateRule.SIMPLE -> it.groupId + it.name
                DuplicateRule.EXACT -> it.groupId + it.name + it.description?.toMD5()
            }
        }
    }

    when (duplicateMode) {
        DuplicateMode.MERGE -> {
            val deDuplicatedList = mutableListOf<Library>()
            mappedLibs().forEach { (_, group) ->
                deDuplicatedList.add(
                    if (group.size > 1) {
                        // on duplicates, assumption is the shorter title is the base dependency
                        group.minByOrNull { it.name?.length ?: it.description?.length ?: Int.MAX_VALUE } ?: group.first()
                    } else {
                        group.first()
                    }
                )
            }
            return deDuplicatedList
        }

        DuplicateMode.LINK -> {
            mappedLibs().forEach { (_, group) ->
                if (group.size > 1) {
                    val allAssociated = group.map { it.uniqueId }
                    group.forEach {
                        it.associated = allAssociated.filter { a -> a == it.uniqueId }
                    }
                }
            }
            return this // we did add association by reference
        }

        DuplicateMode.KEEP -> {
            // no duplication handling enabled
            return this
        }
    }
}

fun Library.merge(with: Library) {
    val orgLib = this
    with.name?.takeIf { it.isNotBlank() }?.also { orgLib.name = it }
    with.description?.takeIf { it.isNotBlank() }?.also { orgLib.description = it }
    with.website?.takeIf { it.isNotBlank() }?.also { orgLib.website = it }
    with.tag?.takeIf { it.isNotBlank() }?.also { orgLib.tag = it }

    // merge custom data with original data
    val origOrganization = orgLib.organization
    val newOrganization = with.organization
    if (origOrganization == null) {
        orgLib.organization = newOrganization
    } else if (newOrganization != null) {
        newOrganization.name?.let { origOrganization.name }
        newOrganization.url?.let { origOrganization.url }
    }

    // merge custom scm data with original data
    val origScm = orgLib.scm
    val newScm = with.scm
    if (origScm == null) {
        orgLib.scm = newScm
    } else if (newScm != null) {
        newScm.connection?.let { origScm.connection }
        newScm.developerConnection?.let { origScm.developerConnection }
        newScm.url?.let { origScm.url }
    }

    // merge developers, based on name (ensure we don't duplicate names)
    val developers = mutableListOf<Developer>().also { it.addAll(orgLib.developers) }
    with.developers.forEach { dev ->
        val existing = developers.firstOrNull { it.name == dev.name }
        if (existing != null) {
            existing.organisationUrl = dev.organisationUrl
        } else {
            developers.add(dev)
        }
    }
    orgLib.developers = developers

    // merge licenses
    orgLib.licenses = mutableSetOf<String>().also {
        it.addAll(with.licenses)
        it.addAll(orgLib.licenses)
    }

    // merge funding
    orgLib.funding = mutableSetOf<Funding>().also {
        it.addAll(with.funding)
        it.addAll(orgLib.funding)
    }
}