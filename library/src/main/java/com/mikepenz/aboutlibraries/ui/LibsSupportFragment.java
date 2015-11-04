package com.mikepenz.aboutlibraries.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.aboutlibraries.LibsFragmentCompat;

/**
 * Created by mikepenz on 04.06.14.
 */
public class LibsSupportFragment extends Fragment {

    private LibsFragmentCompat libsFragmentCompat;

    public LibsSupportFragment() {
        libsFragmentCompat = new LibsFragmentCompat();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return libsFragmentCompat.onCreateView(container.getContext(), inflater, container, savedInstanceState, getArguments());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        libsFragmentCompat.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        libsFragmentCompat.onDestroyView();
        super.onDestroyView();
    }
}
