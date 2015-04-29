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
import com.mikepenz.aboutlibraries.R;
import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.aboutlibraries.ui.adapter.LibsRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by mikepenz on 04.06.14.
 */
public class LibsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private LibsRecyclerViewAdapter mAdapter;

    private ArrayList<Library> libraries;

    private int returnCode = Libs.INVALID_RESULT_CODE;

    private boolean autoDetect = true;
    private boolean sort = true;
    private boolean animate = true;

    private boolean showLicense = false;
    private boolean showLicenseDialog = true;
    private boolean showVersion = false;

    private String aboutAppName = null;
    private String aboutSpecial1 = null;
    private String aboutSpecial1Description = null;
    private String aboutSpecial2 = null;
    private String aboutSpecial2Description = null;
    private String aboutSpecial3 = null;
    private String aboutSpecial3Description = null;
    private Boolean aboutShowIcon = null;
    private Boolean aboutShowVersion = null;
    private Boolean aboutShowVersionName = null;
    private Boolean aboutShowVersionCode = null;
    private String aboutDescription = null;

    private HashMap<String, HashMap<String, String>> libraryModification;

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

        String[] fields = null;
        String[] internalLibraries = null;
        String[] excludeLibraries = null;

        //read and get our arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            fields = bundle.getStringArray(Libs.BUNDLE_FIELDS);
            internalLibraries = bundle.getStringArray(Libs.BUNDLE_LIBS);
            excludeLibraries = bundle.getStringArray(Libs.BUNDLE_EXCLUDE_LIBS);

            autoDetect = bundle.getBoolean(Libs.BUNDLE_AUTODETECT, true);
            sort = bundle.getBoolean(Libs.BUNDLE_SORT, true);
            animate = bundle.getBoolean(Libs.BUNDLE_ANIMATE, true);

            showLicense = bundle.getBoolean(Libs.BUNDLE_LICENSE, false);
            showLicenseDialog = bundle.getBoolean(Libs.BUNDLE_LICENSE_DIALOG, true);
            showVersion = bundle.getBoolean(Libs.BUNDLE_VERSION, false);

            returnCode = bundle.getInt(Libs.BUNDLE_RESULT_CODE);

            try {
                libraryModification = (HashMap<String, HashMap<String, String>>) bundle.getSerializable(Libs.BUNDLE_LIBS_MODIFICATION);
            } catch (Exception ex) {

            }
        }

        //init the Libs instance with fields if they were set
        if (fields == null) {
            libs = new Libs(getActivity());
        } else {
            libs = new Libs(getActivity(), fields);
        }

        //The last step is to look if we would love to show some about text for this project
        aboutShowIcon = extractBooleanBundleOrResource(libs, bundle, Libs.BUNDLE_APP_ABOUT_ICON, "aboutLibraries_description_showIcon");
        aboutShowVersion = extractBooleanBundleOrResource(libs, bundle, Libs.BUNDLE_APP_ABOUT_VERSION, "aboutLibraries_description_showVersion");
        aboutShowVersionName = extractBooleanBundleOrResource(libs, bundle, Libs.BUNDLE_APP_ABOUT_VERSION_NAME, "aboutLibraries_description_showVersionName");
        aboutShowVersionCode = extractBooleanBundleOrResource(libs, bundle, Libs.BUNDLE_APP_ABOUT_VERSION_CODE, "aboutLibraries_description_showVersionCode");

        aboutAppName = extractStringBundleOrResource(libs, bundle, Libs.BUNDLE_APP_ABOUT_NAME, "aboutLibraries_description_name");
        aboutDescription = extractStringBundleOrResource(libs, bundle, Libs.BUNDLE_APP_ABOUT_DESCRIPTION, "aboutLibraries_description_text");

        aboutSpecial1 = extractStringBundleOrResource(libs, bundle, Libs.BUNDLE_APP_ABOUT_SPECIAL1, "aboutLibraries_description_special1_name");
        aboutSpecial1Description = extractStringBundleOrResource(libs, bundle, Libs.BUNDLE_APP_ABOUT_SPECIAL1_DESCRIPTION, "aboutLibraries_description_special1_text");
        aboutSpecial2 = extractStringBundleOrResource(libs, bundle, Libs.BUNDLE_APP_ABOUT_SPECIAL2, "aboutLibraries_description_special2_name");
        aboutSpecial2Description = extractStringBundleOrResource(libs, bundle, Libs.BUNDLE_APP_ABOUT_SPECIAL2_DESCRIPTION, "aboutLibraries_description_special2_text");
        aboutSpecial3 = extractStringBundleOrResource(libs, bundle, Libs.BUNDLE_APP_ABOUT_SPECIAL3, "aboutLibraries_description_special3_name");
        aboutSpecial3Description = extractStringBundleOrResource(libs, bundle, Libs.BUNDLE_APP_ABOUT_SPECIAL3_DESCRIPTION, "aboutLibraries_description_special3_text");

        //apply modifications
        libs.modifyLibraries(libraryModification);

        //fetch the libraries and sort if a comparator was set
        libraries = libs.prepareLibraries(internalLibraries, excludeLibraries, autoDetect, sort);

        if (comparator != null) {
            Collections.sort(libraries, comparator);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_opensource, container, false);

        // init CardView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.cardListView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(LibsFragment.this.getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new LibsRecyclerViewAdapter(getActivity(), showLicense, showLicenseDialog, showVersion);
        mAdapter.setReturnCode(returnCode);
        mRecyclerView.setAdapter(mAdapter);

        generateAboutThisAppSection();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter.addLibs(libraries);

        if (animate) {
            Animation fadeIn = AnimationUtils.loadAnimation(LibsFragment.this.getActivity(), android.R.anim.slide_in_left);
            fadeIn.setDuration(500);
            LayoutAnimationController layoutAnimationController = new LayoutAnimationController(fadeIn);
            mRecyclerView.setLayoutAnimation(layoutAnimationController);
            mRecyclerView.startLayoutAnimation();
        }

        super.onViewCreated(view, savedInstanceState);
    }

    private void generateAboutThisAppSection() {
        if (aboutShowIcon != null && (aboutShowVersion != null || aboutShowVersionName != null || aboutShowVersionCode)) {
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
            if (aboutShowIcon && appInfo != null) {
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
            mAdapter.setHeader(aboutAppName, aboutDescription, aboutSpecial1, aboutSpecial1Description, aboutSpecial2, aboutSpecial2Description, aboutSpecial3, aboutSpecial3Description, versionName, versionCode, aboutShowVersion, aboutShowVersionName, aboutShowVersionCode, icon, aboutShowIcon);
        }
    }

    /**
     * Helper to extract a boolean from a bundle or resource
     *
     * @param libs
     * @param bundle
     * @param bundleKey
     * @param resName
     * @return
     */
    private Boolean extractBooleanBundleOrResource(Libs libs, Bundle bundle, String bundleKey, String resName) {
        Boolean result = null;
        if (bundle != null && bundle.containsKey(bundleKey)) {
            result = bundle.getBoolean(bundleKey);
        } else {
            String descriptionShowVersion = libs.getStringResourceByName(resName);
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
     * @param bundle
     * @param bundleKey
     * @param resName
     * @return
     */
    private String extractStringBundleOrResource(Libs libs, Bundle bundle, String bundleKey, String resName) {
        String result = null;
        if (bundle != null && bundle.containsKey(bundleKey)) {
            result = bundle.getString(bundleKey);
        } else {
            String descriptionShowVersion = libs.getStringResourceByName(resName);
            if (!TextUtils.isEmpty(descriptionShowVersion)) {
                result = descriptionShowVersion;
            }
        }
        return result;
    }
}
