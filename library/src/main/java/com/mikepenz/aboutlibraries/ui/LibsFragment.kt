package com.mikepenz.aboutlibraries.ui

import android.annotation.TargetApi
import android.app.Fragment
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.aboutlibraries.LibsFragmentCompat


/**
 * Created by mikepenz on 04.06.14.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class LibsFragment : Fragment() {

    private val libsFragmentCompat: LibsFragmentCompat = LibsFragmentCompat()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return libsFragmentCompat.onCreateView(inflater.context, inflater, container, savedInstanceState, arguments)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        libsFragmentCompat.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        libsFragmentCompat.onDestroyView()
        super.onDestroyView()
    }
}
