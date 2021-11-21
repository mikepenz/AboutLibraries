package com.mikepenz.aboutlibraries.ui.compose.util

import com.mikepenz.aboutlibraries.entity.Library

val Library.author: String
    get() = developers.takeIf { it.isNotEmpty() }?.map { it.name }?.joinToString(", ") ?: organization?.name ?: ""
