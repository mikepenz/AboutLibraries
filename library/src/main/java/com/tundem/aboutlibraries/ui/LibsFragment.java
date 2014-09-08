package com.tundem.aboutlibraries.ui;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tundem.aboutlibraries.Libs;
import com.tundem.aboutlibraries.R;
import com.tundem.aboutlibraries.entity.Library;
import com.tundem.aboutlibraries.ui.adapter.LibsListViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by mikepenz on 04.06.14.
 */
public class LibsFragment extends Fragment {

    private Libs libs;
    private ListView listView;
    private ArrayList<Library> libraries;

    private boolean autoDetect = false;
    private boolean sort = true;

    private boolean showLicense = false;
    private boolean showLicenseDialog = true;
    private boolean showVersion = false;

    private Boolean aboutShowIcon = null;
    private Boolean aboutShowVersion = null;
    private Spanned aboutDescription = null;

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

        String[] fields = null;
        String[] internalLibraries = null;
        String[] excludeLibraries = null;

        //read and get our arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            fields = bundle.getStringArray(Libs.BUNDLE_FIELDS);
            internalLibraries = bundle.getStringArray(Libs.BUNDLE_LIBS);
            excludeLibraries = bundle.getStringArray(Libs.BUNDLE_EXCLUDE_LIBS);

            autoDetect = bundle.getBoolean(Libs.BUNDLE_AUTODETECT, false);
            sort = bundle.getBoolean(Libs.BUNDLE_SORT, true);

            showLicense = bundle.getBoolean(Libs.BUNDLE_LICENSE, false);
            showLicenseDialog = bundle.getBoolean(Libs.BUNDLE_LICENSE_DIALOG, true);
            showVersion = bundle.getBoolean(Libs.BUNDLE_VERSION, false);
        }

        //init the Libs instance with fields if they were set
        if (fields == null) {
            libs = Libs.getInstance(activity);
        } else {
            libs = Libs.getInstance(activity, fields);
        }

        //The last step is to look if we would love to show some about text for this project
        String descriptionShowIcon = libs.getStringResourceByName("aboutLibraries_description_showIcon");
        if (!TextUtils.isEmpty(descriptionShowIcon)) {
            try {
                aboutShowIcon = Boolean.parseBoolean(descriptionShowIcon);
            } catch (Exception ex) {
            }
        }
        String descriptionShowVersion = libs.getStringResourceByName("aboutLibraries_description_showVersion");
        if (!TextUtils.isEmpty(descriptionShowIcon)) {
            try {
                aboutShowVersion = Boolean.parseBoolean(descriptionShowVersion);
            } catch (Exception ex) {
            }
        }

        aboutDescription = Html.fromHtml(libs.getStringResourceByName("aboutLibraries_description_text"));

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
        listView = (ListView) view.findViewById(R.id.cardListView);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        generateAboutThisAppSection();

        listView.setAdapter(new LibsListViewAdapter(getActivity(), libraries, showLicense, showLicenseDialog, showVersion));
        super.onViewCreated(view, savedInstanceState);
    }

    private void generateAboutThisAppSection() {
        if (aboutShowIcon != null && aboutShowVersion != null) {
            View headerView = getActivity().getLayoutInflater().inflate(R.layout.listheader_opensource, null);

            //get the about this app views
            ImageView aboutIcon = (ImageView) headerView.findViewById(R.id.aboutIcon);
            TextView aboutVersion = (TextView) headerView.findViewById(R.id.aboutVersion);
            View aboutDivider = headerView.findViewById(R.id.aboutDivider);
            TextView aboutAppDescription = (TextView) headerView.findViewById(R.id.aboutDescription);

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
            if (aboutShowIcon && appInfo != null) {
                aboutIcon.setImageDrawable(appInfo.loadIcon(pm));
            } else {
                aboutIcon.setVisibility(View.GONE);
            }

            //set the Version or hide it
            if (aboutShowVersion && packageInfo != null) {
                String versionName = packageInfo.versionName;
                int versionCode = packageInfo.versionCode;
                aboutVersion.setText(getString(R.string.version) + " " + versionName + " (" + versionCode + ")");
            } else {
                aboutVersion.setVisibility(View.GONE);
            }

            //Set the description or hide it
            if (!TextUtils.isEmpty(aboutDescription)) {
                aboutAppDescription.setText(aboutDescription);
            } else {
                aboutAppDescription.setVisibility(View.GONE);
            }

            //if there is no description or no icon and version number hide the divider
            if (!aboutShowIcon && !aboutShowVersion || TextUtils.isEmpty(aboutDescription)) {
                aboutDivider.setVisibility(View.GONE);
            }

            //add this cool thing to the headerView of our listView
            listView.addHeaderView(headerView, null, false);
        }
    }
}
