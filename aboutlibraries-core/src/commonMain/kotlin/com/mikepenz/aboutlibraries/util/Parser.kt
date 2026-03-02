package com.mikepenz.aboutlibraries.util

import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License

expect fun parseData(json: String): Result

class Result(
    val libraries: List<Library>,
    val licenses: List<License>
) {
    operator fun component1() = libraries
    operator fun component2() = licenses
}