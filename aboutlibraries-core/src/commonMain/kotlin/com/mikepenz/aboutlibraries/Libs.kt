package com.mikepenz.aboutlibraries

import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.util.parseData

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

    class Builder() {
        private var _stringData: String? = null

        fun withJson(stringData: String): Builder {
            _stringData = stringData
            return this
        }

        fun build(): Libs {
            return Libs(_stringData)
        }
    }
}
