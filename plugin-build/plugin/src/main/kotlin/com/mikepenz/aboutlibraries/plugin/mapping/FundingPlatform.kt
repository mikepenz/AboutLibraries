package com.mikepenz.aboutlibraries.plugin.mapping

/**
 * Enum representing different funding platforms supported by GitHub and other services.
 */
enum class FundingPlatform(val id: String) {
    GITHUB("GITHUB"),
    PATREON("PATREON"),
    OPEN_COLLECTIVE("OPEN_COLLECTIVE"),
    KO_FI("KO_FI"),
    TIDELIFT("TIDELIFT"),
    COMMUNITY_BRIDGE("COMMUNITY_BRIDGE"),
    LIBERAPAY("LIBERAPAY"),
    ISSUEHUNT("ISSUEHUNT"),
    OTECHIE("OTECHIE"),
    CUSTOM("CUSTOM"),
    OTHER("OTHER");

    /**
     * Formats the value according to the platform's URL format.
     *
     * @param value The value to format
     * @return The formatted URL
     */
    fun formatUrl(value: String): String {
        return when (this) {
            GITHUB -> if (value.contains("/")) value else "https://github.com/sponsors/$value"
            PATREON -> if (value.startsWith("http")) value else "https://patreon.com/$value"
            OPEN_COLLECTIVE -> if (value.startsWith("http")) value else "https://opencollective.com/$value"
            KO_FI -> if (value.startsWith("http")) value else "https://ko-fi.com/$value"
            LIBERAPAY -> if (value.startsWith("http")) value else "https://liberapay.com/$value"
            ISSUEHUNT -> if (value.startsWith("http")) value else "https://issuehunt.io/r/$value"
            else -> value
        }
    }

    companion object {
        /**
         * Converts a string platform name to the corresponding FundingPlatform enum.
         *
         * @param platform The platform name as a string
         * @return The corresponding FundingPlatform enum
         */
        fun fromString(platform: String): FundingPlatform {
            return when (platform.lowercase()) {
                "github" -> GITHUB
                "patreon" -> PATREON
                "open_collective" -> OPEN_COLLECTIVE
                "ko_fi" -> KO_FI
                "tidelift" -> TIDELIFT
                "community_bridge" -> COMMUNITY_BRIDGE
                "liberapay" -> LIBERAPAY
                "issuehunt" -> ISSUEHUNT
                "otechie" -> OTECHIE
                "custom" -> CUSTOM
                else -> OTHER
            }
        }
    }
}