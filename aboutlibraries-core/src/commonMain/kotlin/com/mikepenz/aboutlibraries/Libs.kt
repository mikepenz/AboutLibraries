package com.mikepenz.aboutlibraries

import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.util.parseData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The [Libs] class is the main access point to the generated data of the plugin.
 * Provides accessors for the [Library] and [License] lists, containing all the dependency information for the module.
 */
@Serializable
data class Libs constructor(
    @SerialName("libraries") val libraries: List<Library>,
    @SerialName("licenses") val licenses: Set<License>,
) {
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
            val data = _stringData
            val (libraries, licenses) = if (data != null) {
                parseData(data)
            } else {
                throw IllegalStateException(
                    """
                    Please provide the required library data via the available APIs.
                    Depending on the platform this can be done for example via `Libs.Builder().withJson()`.
                    For Android there exists an `Libs.Builder().withContext(context).build()`, automatically loading the `aboutlibraries.json` file from the `raw` resources folder.
                    When using compose or other parent modules, please check their corresponding APIs.
                """.trimIndent()
                )
            }
            return Libs(
                libraries.sortedBy { it.name.lowercase() },
                licenses.toSet()
            )
        }
    }
}
