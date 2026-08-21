package com.mikepenz.aboutlibraries.plugin.util

import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import com.mikepenz.aboutlibraries.plugin.mapping.Developer
import com.mikepenz.aboutlibraries.plugin.mapping.Funding
import com.mikepenz.aboutlibraries.plugin.mapping.Library

fun List<Library>.processDuplicates(
    duplicateMode: DuplicateMode,
    duplicateRule: DuplicateRule,
    rootIds: Map<String, String> = emptyMap(),
): List<Library> {
    fun mappedLibs(): List<List<Library>> {
        return this.groupBy {
            when (duplicateRule) {
                DuplicateRule.GROUP -> it.groupId + it.licenses.joinToString(",")
                DuplicateRule.SIMPLE -> it.groupId + it.name
                DuplicateRule.EXACT -> it.groupId + it.name + it.description?.toMD5()
            }
        }.values.flatMap { group ->
            // one cluster per module: platform artifacts fall onto the root they redirect from,
            // everything else onto its own id
            group.groupBy { rootIds[it.uniqueId] ?: it.uniqueId }.values
        }
    }

    when (duplicateMode) {
        DuplicateMode.MERGE -> {
            val deDuplicatedList = mutableListOf<Library>()
            mappedLibs().forEach { group ->
                val kept = if (group.size > 1) {
                    // on duplicates, assumption is the shorter title is the base dependency; on a
                    // tie (a KMP publication names every platform artifact identically) the
                    // shortest id is the root module the others are platform variants of
                    group.minWithOrNull(
                        compareBy({ it.name?.length ?: it.description?.length ?: Int.MAX_VALUE }, { it.uniqueId.length })
                    ) ?: group.first()
                } else {
                    group.first()
                }
                // the discarded siblings may have been consumed by targets the survivor was not
                // part of; retain the union so no target is silently lost
                if (group.size > 1 && group.any { it.targets != null }) {
                    kept.targets = group.flatMapTo(sortedSetOf()) { it.targets.orEmpty() }
                }
                deDuplicatedList.add(kept)
            }
            return deDuplicatedList
        }

        DuplicateMode.LINK -> {
            mappedLibs().forEach { group ->
                if (group.size > 1) {
                    val allAssociated = group.map { it.uniqueId }
                    group.forEach {
                        // the *other* members of the group — a library is not associated to itself
                        it.associated = allAssociated.filter { a -> a != it.uniqueId }
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

    // merge targets, keeping `null` (== feature disabled) if neither side provides any
    if (orgLib.targets != null || with.targets != null) {
        orgLib.targets = sortedSetOf<String>().also {
            it.addAll(with.targets.orEmpty())
            it.addAll(orgLib.targets.orEmpty())
        }
    }
}
