package com.tundem.aboutlibraries.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tundem.aboutlibraries.Libs;
import com.tundem.aboutlibraries.R;
import com.tundem.aboutlibraries.entity.Library;
import com.tundem.aboutlibraries.ui.adapter.LibsListViewAdapter;

import java.util.ArrayList;

/**
 * Created by mikepenz on 04.06.14.
 */
public class LibsFragment extends Fragment {

    private Libs libs;
    private ListView listView;
    private ArrayList<Library> libraries;


    /**
     * Default Constructor
     * Gets an libs instance and gets all external libs
     */
    public LibsFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        String[] fields = null;
        String[] internalLibraries = null;

        //read and get our arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            internalLibraries = bundle.getStringArray(Libs.BUNDLE_LIBS);
            fields = bundle.getStringArray(Libs.BUNDLE_FIELDS);
        }

        //init the Libs instance with fields if they were set
        if (fields == null) {
            libs = Libs.getInstance(activity);
        } else {
            libs = Libs.getInstance(activity, fields);
        }

        //Add all external libraries
        libraries = libs.getExternLibraries();

        //Now add all libs which do not contains the info file, but are in the AboutLibraires lib
        if (internalLibraries != null) {
            for (String internalLibrary : internalLibraries) {
                Library lib = libs.getLibrary(internalLibrary);
                if (lib != null) {
                    libraries.add(lib);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_opensource, container, false);

        // init CardView
        listView = (ListView) view.findViewById(R.id.cardListView);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        listView.setAdapter(new LibsListViewAdapter(getActivity(), libraries));
        super.onViewCreated(view, savedInstanceState);
    }
}
