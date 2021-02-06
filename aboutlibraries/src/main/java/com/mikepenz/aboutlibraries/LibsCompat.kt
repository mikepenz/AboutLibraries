@file:JvmName("LibsCompat")

package com.mikepenz.aboutlibraries

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import com.mikepenz.aboutlibraries.ui.LibsFragment

/**
 * Created by mikepenz on 18.03.16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
object LibsCompat {

    /**
     * fragment() method to build and create the fragment with the set params
     *
     * @return the fragment to set in your application
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun fragment(libsBuilder: LibsBuilder): LibsFragment {
        val bundle = Bundle()
        bundle.putSerializable("data", libsBuilder)

        val fragment = LibsFragment()
        fragment.arguments = bundle
        return fragment
    }
}
