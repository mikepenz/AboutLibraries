package com.mikepenz.aboutlibraries.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.ui.LibsSupportFragment

// Make a similar class to create a destination in Jetpack Navigation
class NavHostDestinationFragment : LibsSupportFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        with(Bundle()) {
            // customize LibsBuilder object as needed
            putSerializable("data", LibsBuilder())
            return libsFragmentCompat.onCreateView(inflater.context, inflater, container, savedInstanceState, this)
        }
    }
}