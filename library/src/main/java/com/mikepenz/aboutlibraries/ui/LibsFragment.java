package com.mikepenz.aboutlibraries.ui;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.LibsConfiguration;
import com.mikepenz.aboutlibraries.R;
import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.aboutlibraries.ui.adapter.LibsRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by mikepenz on 04.06.14.
 */
public class LibsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private LibsRecyclerViewAdapter mAdapter;

    LibsBuilder builder = null;

    private ArrayList<Library> libraries;

    private Comparator<Library> comparator;

    /**
     * Default Constructor
     * Gets an libs instance and gets all external libs
     */
    public LibsFragment() {
    }

    public void setLibraryComparator(final Comparator<Library> comparator) {
        this.comparator = comparator;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Libs libs;

        //read and get our arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            builder = (LibsBuilder) bundle.getSerializable("data");
        }

        //init the Libs instance with fields if they were set
        if (builder.fields == null) {
            libs = new Libs(getActivity());
        } else {
            libs = new Libs(getActivity(), builder.fields);
        }

        //The last step is to look if we would love to show some about text for this project
        builder.aboutShowIcon = extractBooleanBundleOrResource(libs, builder.aboutShowIcon, "aboutLibraries_description_showIcon");
        builder.aboutShowVersion = extractBooleanBundleOrResource(libs, builder.aboutShowVersion, "aboutLibraries_description_showVersion");
        builder.aboutShowVersionName = extractBooleanBundleOrResource(libs, builder.aboutShowVersionName, "aboutLibraries_description_showVersionName");
        builder.aboutShowVersionCode = extractBooleanBundleOrResource(libs, builder.aboutShowVersionCode, "aboutLibraries_description_showVersionCode");

        builder.aboutAppName = extractStringBundleOrResource(libs, builder.aboutAppName, "aboutLibraries_description_name");
        builder.aboutDescription = extractStringBundleOrResource(libs, builder.aboutDescription, "aboutLibraries_description_text");

        builder.aboutAppSpecial1 = extractStringBundleOrResource(libs, builder.aboutAppSpecial1, "aboutLibraries_description_special1_name");
        builder.aboutAppSpecial1Description = extractStringBundleOrResource(libs, builder.aboutAppSpecial1Description, "aboutLibraries_description_special1_text");
        builder.aboutAppSpecial2 = extractStringBundleOrResource(libs, builder.aboutAppSpecial2, "aboutLibraries_description_special2_name");
        builder.aboutAppSpecial2Description = extractStringBundleOrResource(libs, builder.aboutAppSpecial2Description, "aboutLibraries_description_special2_text");
        builder.aboutAppSpecial3 = extractStringBundleOrResource(libs, builder.aboutAppSpecial3, "aboutLibraries_description_special3_name");
        builder.aboutAppSpecial3Description = extractStringBundleOrResource(libs, builder.aboutAppSpecial3Description, "aboutLibraries_description_special3_text");

        //apply modifications
        libs.modifyLibraries(builder.libraryModification);

        //fetch the libraries and sort if a comparator was set
        boolean doDefaultSort = (builder.sort && null == builder.libraryComparator && null == comparator);
        libraries = libs.prepareLibraries(getActivity(), builder.internalLibraries, builder.excludeLibraries, builder.autoDetect, doDefaultSort);

        if (comparator != null) {
            Collections.sort(libraries, comparator);
        } else if (builder.libraryComparator != null) {
            Collections.sort(libraries, builder.libraryComparator);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_opensource, container, false);

        //allows to modify the view before creating
        if (LibsConfiguration.getInstance().getUiListener() != null) {
            view = LibsConfiguration.getInstance().getUiListener().preOnCreateView(view);
        }

        // init CardView
        if (view.getId() == R.id.cardListView) {
            mRecyclerView = (RecyclerView) view;
        } else {
            mRecyclerView = (RecyclerView) view.findViewById(R.id.cardListView);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new LibsRecyclerViewAdapter(builder);
        mRecyclerView.setAdapter(mAdapter);

        generateAboutThisAppSection();

        //allows to modify the view after creating
        if (LibsConfiguration.getInstance().getUiListener() != null) {
            view = LibsConfiguration.getInstance().getUiListener().postOnCreateView(view);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter.addLibs(libraries);

        if (builder.animate) {
            LayoutAnimationController layoutAnimationController;

            if (LibsConfiguration.getInstance().getLayoutAnimationController() == null) {
                Animation fadeIn = AnimationUtils.loadAnimation(LibsFragment.this.getActivity(), android.R.anim.slide_in_left);
                fadeIn.setDuration(500);
                layoutAnimationController = new LayoutAnimationController(fadeIn);
            } else {
                layoutAnimationController = LibsConfiguration.getInstance().getLayoutAnimationController();
            }

            mRecyclerView.setLayoutAnimation(layoutAnimationController);
            mRecyclerView.startLayoutAnimation();
        }

        super.onViewCreated(view, savedInstanceState);
    }

    private void generateAboutThisAppSection() {
        if (builder.aboutShowIcon != null && (builder.aboutShowVersion != null || builder.aboutShowVersionName != null || builder.aboutShowVersionCode)) {
            //get the packageManager to load and read some values :D
            PackageManager pm = getActivity().getPackageManager();
            //get the packageName
            String packageName = getActivity().getPackageName();
            //Try to load the applicationInfo
            ApplicationInfo appInfo = null;
            PackageInfo packageInfo = null;
            try {
                appInfo = pm.getApplicationInfo(packageName, 0);
                packageInfo = pm.getPackageInfo(packageName, 0);
            } catch (Exception ex) {
            }

            //Set the Icon or hide it
            Drawable icon = null;
            if (builder.aboutShowIcon && appInfo != null) {
                icon = appInfo.loadIcon(pm);
            }

            //set the Version or hide it
            String versionName = null;
            Integer versionCode = null;
            if (packageInfo != null) {
                versionName = packageInfo.versionName;
                versionCode = packageInfo.versionCode;
            }

            //add this cool thing to the headerView of our listView
            mAdapter.setHeader(versionName, versionCode, icon);
        }
    }

    /**
     * Helper to extract a boolean from a bundle or resource
     *
     * @param libs
     * @param value
     * @param resName
     * @return
     */
    private Boolean extractBooleanBundleOrResource(Libs libs, Boolean value, String resName) {
        Boolean result = null;
        if (value != null) {
            result = value;
        } else {
            String descriptionShowVersion = libs.getStringResourceByName(getActivity(), resName);
            if (!TextUtils.isEmpty(descriptionShowVersion)) {
                try {
                    result = Boolean.parseBoolean(descriptionShowVersion);
                } catch (Exception ex) {
                }
            }
        }
        return result;
    }

    /**
     * Helper to extract a string from a bundle or resource
     *
     * @param libs
     * @param value
     * @param resName
     * @return
     */
    private String extractStringBundleOrResource(Libs libs, String value, String resName) {
        String result = null;
        if (value != null) {
            result = value;
        } else {
            String descriptionShowVersion = libs.getStringResourceByName(getActivity(), resName);
            if (!TextUtils.isEmpty(descriptionShowVersion)) {
                result = descriptionShowVersion;
            }
        }
        return result;
    }
}
