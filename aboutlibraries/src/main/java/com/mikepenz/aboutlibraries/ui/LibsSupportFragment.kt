package com.mikepenz.aboutlibraries.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.Fragment
import com.mikepenz.aboutlibraries.LibsFragmentCompat


/**
 * Created by mikepenz on 04.06.14.
 */
open class LibsSupportFragment : Fragment(), Filterable {

    protected val libsFragmentCompat: LibsFragmentCompat = LibsFragmentCompat()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return libsFragmentCompat.onCreateView(inflater.context, inflater, container, savedInstanceState, arguments)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        libsFragmentCompat.onViewCreated(view)
    }

    override fun onDestroyView() {
        libsFragmentCompat.onDestroyView()
        super.onDestroyView()
    }

    override fun getFilter(): Filter = libsFragmentCompat.filter
}
