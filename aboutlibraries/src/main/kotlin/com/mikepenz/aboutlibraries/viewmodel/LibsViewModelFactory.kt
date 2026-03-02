@file:Suppress("DEPRECATION")

package com.mikepenz.aboutlibraries.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder

class LibsViewModelFactory(
    private val context: Context,
    private val builder: LibsBuilder, // ui module
    private val libsBuilder: Libs.Builder,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return LibsViewModel(context, builder, libsBuilder) as T
    }
}