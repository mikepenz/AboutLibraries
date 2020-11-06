package com.mikepenz.aboutlibraries.util

import android.view.View
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsConfiguration
import com.mikepenz.aboutlibraries.entity.Library

abstract class LibsListenerImpl : LibsConfiguration.LibsListener {
    override fun onIconClicked(v: View) {
    }

    override fun onLibraryAuthorClicked(v: View, library: Library): Boolean {
        return false
    }

    override fun onLibraryContentClicked(v: View, library: Library): Boolean {
        return false
    }

    override fun onLibraryBottomClicked(v: View, library: Library): Boolean {
        return false
    }

    override fun onExtraClicked(v: View, specialButton: Libs.SpecialButton): Boolean {
        return false
    }

    override fun onIconLongClicked(v: View): Boolean {
        return true
    }

    override fun onLibraryAuthorLongClicked(v: View, library: Library): Boolean {
        return true
    }

    override fun onLibraryContentLongClicked(v: View, library: Library): Boolean {
        return true
    }

    override fun onLibraryBottomLongClicked(v: View, library: Library): Boolean {
        return true
    }
}