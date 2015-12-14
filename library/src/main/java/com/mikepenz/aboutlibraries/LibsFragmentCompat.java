package com.mikepenz.aboutlibraries;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.aboutlibraries.ui.adapter.LibsRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by mikepenz on 02.11.15.
 */
public class LibsFragmentCompat {
    private RecyclerView mRecyclerView;
    private LibsRecyclerViewAdapter mAdapter;

    private LibsBuilder builder = null;
    private static ArrayList<Library> libraries;
    private Comparator<Library> comparator;
    private LibraryTask mLibTask;

    /**
     * Default Constructor
     * Gets an libs instance and gets all external libs
     */
    public LibsFragmentCompat() {
    }

    public void setLibraryComparator(final Comparator<Library> comparator) {
        this.comparator = comparator;
    }

    public View onCreateView(Context context, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, Bundle arguments) {
        if (arguments != null) {
            builder = (LibsBuilder) arguments.getSerializable("data");
        } else {
            Log.e("AboutLibraries", "The AboutLibraries fragment can't be build without the bundle containing the LibsBuilder");
        }

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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        if (LibsConfiguration.getInstance().getItemAnimator() != null) {
            mRecyclerView.setItemAnimator(LibsConfiguration.getInstance().getItemAnimator());
        } else {
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        }

        if (builder != null) {
            mAdapter = new LibsRecyclerViewAdapter(builder);
            mAdapter.setHasStableIds(true);
            mRecyclerView.setAdapter(mAdapter);
        }

        //allows to modify the view after creating
        if (LibsConfiguration.getInstance().getUiListener() != null) {
            view = LibsConfiguration.getInstance().getUiListener().postOnCreateView(view);
        }

        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        //load the data (only possible if we were able to get the Arguments
        if (view.getContext() != null && builder != null) {
            //fill the fragment with the content
            mLibTask = new LibraryTask(view.getContext());
            executeLibTask(mLibTask);
        }
    }

    protected void executeLibTask(LibraryTask libraryTask) {
        if (libraryTask != null) {
            if (Build.VERSION.SDK_INT >= 11) {
                switch (builder.libTaskExecutor) {
                    case THREAD_POOL_EXECUTOR:
                        libraryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        break;
                    case SERIAL_EXECUTOR:
                        libraryTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                        break;
                    case DEFAULT_EXECUTOR:
                    default:
                        libraryTask.execute();
                        break;
                }
            } else {
                libraryTask.execute();
            }
        }
    }


    public void onDestroyView() {
        if (mLibTask != null) {
            mLibTask.cancel(true);
            mLibTask = null;
        }
    }

    public class LibraryTask extends AsyncTask<String, String, String> {
        Context ctx;

        String versionName;
        Integer versionCode;
        Drawable icon = null;

        public LibraryTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            if(builder.libTaskCallback != null) builder.libTaskCallback.onLibTaskStarted();
        }

        @Override
        protected String doInBackground(String... strings) {
            //init the Libs instance with fields if they were set
            Libs libs;
            if (builder.fields == null) {
                libs = new Libs(ctx);
            } else {
                libs = new Libs(ctx, builder.fields);
            }

            //fill the builder with the information
            builder.aboutShowIcon = extractBooleanBundleOrResource(ctx, libs, builder.aboutShowIcon, "aboutLibraries_description_showIcon");
            builder.aboutShowVersion = extractBooleanBundleOrResource(ctx, libs, builder.aboutShowVersion, "aboutLibraries_description_showVersion");
            builder.aboutShowVersionName = extractBooleanBundleOrResource(ctx, libs, builder.aboutShowVersionName, "aboutLibraries_description_showVersionName");
            builder.aboutShowVersionCode = extractBooleanBundleOrResource(ctx, libs, builder.aboutShowVersionCode, "aboutLibraries_description_showVersionCode");

            builder.aboutAppName = extractStringBundleOrResource(ctx, libs, builder.aboutAppName, "aboutLibraries_description_name");
            builder.aboutDescription = extractStringBundleOrResource(ctx, libs, builder.aboutDescription, "aboutLibraries_description_text");

            builder.aboutAppSpecial1 = extractStringBundleOrResource(ctx, libs, builder.aboutAppSpecial1, "aboutLibraries_description_special1_name");
            builder.aboutAppSpecial1Description = extractStringBundleOrResource(ctx, libs, builder.aboutAppSpecial1Description, "aboutLibraries_description_special1_text");
            builder.aboutAppSpecial2 = extractStringBundleOrResource(ctx, libs, builder.aboutAppSpecial2, "aboutLibraries_description_special2_name");
            builder.aboutAppSpecial2Description = extractStringBundleOrResource(ctx, libs, builder.aboutAppSpecial2Description, "aboutLibraries_description_special2_text");
            builder.aboutAppSpecial3 = extractStringBundleOrResource(ctx, libs, builder.aboutAppSpecial3, "aboutLibraries_description_special3_name");
            builder.aboutAppSpecial3Description = extractStringBundleOrResource(ctx, libs, builder.aboutAppSpecial3Description, "aboutLibraries_description_special3_text");

            //only if the libs were not loaded before
            if (libraries == null) {
                //apply modifications
                libs.modifyLibraries(builder.libraryModification);

                //fetch the libraries and sort if a comparator was set
                boolean doDefaultSort = (builder.sort && null == builder.libraryComparator && null == comparator);

                libraries = libs.prepareLibraries(ctx, builder.internalLibraries, builder.excludeLibraries, builder.autoDetect, doDefaultSort);

                if (comparator != null) {
                    Collections.sort(libraries, comparator);
                } else if (builder.libraryComparator != null) {
                    Collections.sort(libraries, builder.libraryComparator);
                }
            }

            //load the data for the header
            if (builder.aboutShowIcon != null && (builder.aboutShowVersion != null || builder.aboutShowVersionName != null || builder.aboutShowVersionCode)) {
                //get the packageManager to load and read some values :D
                PackageManager pm = ctx.getPackageManager();
                //get the packageName
                String packageName = ctx.getPackageName();
                //Try to load the applicationInfo
                ApplicationInfo appInfo = null;
                PackageInfo packageInfo = null;
                try {
                    appInfo = pm.getApplicationInfo(packageName, 0);
                    packageInfo = pm.getPackageInfo(packageName, 0);
                } catch (Exception ex) {
                }

                //Set the Icon or hide it
                if (builder.aboutShowIcon && appInfo != null) {
                    icon = appInfo.loadIcon(pm);
                }

                //set the Version or hide it
                versionName = null;
                versionCode = null;
                if (packageInfo != null) {
                    versionName = packageInfo.versionName;
                    versionCode = packageInfo.versionCode;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //Add the header
            if (builder.aboutShowIcon != null && (builder.aboutShowVersion != null || builder.aboutShowVersionName != null || builder.aboutShowVersionCode)) {
                //add this cool thing to the headerView of our listView
                mAdapter.setHeader(versionName, versionCode, icon);
            }

            //add the libs
            mAdapter.addLibs(libraries);

            //animate filling the list
            if (builder.slideInAnimation) {
                LayoutAnimationController layoutAnimationController;

                if (LibsConfiguration.getInstance().getLayoutAnimationController() == null) {
                    Animation fadeIn = AnimationUtils.loadAnimation(ctx, android.R.anim.slide_in_left);
                    fadeIn.setDuration(500);
                    layoutAnimationController = new LayoutAnimationController(fadeIn);
                } else {
                    layoutAnimationController = LibsConfiguration.getInstance().getLayoutAnimationController();
                }

                mRecyclerView.setLayoutAnimation(layoutAnimationController);
                mRecyclerView.startLayoutAnimation();
            }

            super.onPostExecute(s);

            //forget the context
            ctx = null;
            if(builder.libTaskCallback != null) builder.libTaskCallback.onLibTaskFinished();
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
    private Boolean extractBooleanBundleOrResource(Context ctx, Libs libs, Boolean value, String resName) {
        Boolean result = null;
        if (value != null) {
            result = value;
        } else {
            String descriptionShowVersion = libs.getStringResourceByName(ctx, resName);
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
    private String extractStringBundleOrResource(Context ctx, Libs libs, String value, String resName) {
        String result = null;
        if (value != null) {
            result = value;
        } else {
            String descriptionShowVersion = libs.getStringResourceByName(ctx, resName);
            if (!TextUtils.isEmpty(descriptionShowVersion)) {
                result = descriptionShowVersion;
            }
        }
        return result;
    }
}
