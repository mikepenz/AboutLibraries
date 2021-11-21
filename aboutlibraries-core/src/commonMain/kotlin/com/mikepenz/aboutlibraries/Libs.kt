package com.mikepenz.aboutlibraries

import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.util.parseData

/**
 * The [Libs] class is the main access point to the generated data of the plugin.
 * Provides accessors for the [Library] and [License] lists, containing all the dependency information for the module.
 */
class Libs internal constructor(
    stringData: String? = null
) {

    private val _libraries = mutableListOf<Library>()
    private val _licenses = mutableSetOf<License>()

    /**
     *
     */
    val libraries: List<Library>
        get() = _libraries


    val licenses: Set<License>
        get() = _licenses

    /**
     * init method
     */
    init {
        val (libraries, licenses) = if (stringData != null) {
            parseData(stringData)
        } else {
            throw IllegalStateException("Please provide the data via the provided APIs")
        }

        _libraries.addAll(libraries.sortedBy { it.name })
        _licenses.addAll(licenses)
    }

    /**
     * Builder used to automatically parse and interpret the generated library data from the plugin.
     */
    class Builder {
        private var _stringData: String? = null

        /**
         * Provide the generated library data as [String]
         */
        fun withJson(stringData: String): Builder {
            _stringData = stringData
            return this
        }

        /**
         * Build the [Libs] instance with the applied configuration.
         */
        fun build(): Libs {
            return Libs(_stringData)
        }
    }
}
