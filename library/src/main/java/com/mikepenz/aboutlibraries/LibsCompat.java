package com.mikepenz.aboutlibraries;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;

import com.mikepenz.aboutlibraries.ui.LibsFragment;

/**
 * Created by mikepenz on 18.03.16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LibsCompat {

    /**
     * fragment() method to build and create the fragment with the set params
     *
     * @return the fragment to set in your application
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static LibsFragment fragment(LibsBuilder libsBuilder) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", libsBuilder);

        LibsFragment fragment = new LibsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}
