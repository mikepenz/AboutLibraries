package com.mikepenz.aboutlibraries;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.animation.LayoutAnimationController;

import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.aboutlibraries.ui.LibsActivity;
import com.mikepenz.aboutlibraries.ui.LibsFragment;
import com.mikepenz.aboutlibraries.ui.LibsSupportFragment;
import com.mikepenz.aboutlibraries.ui.item.LibraryItem;
import com.mikepenz.aboutlibraries.util.Colors;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class LibsBuilder implements Serializable {
    public String[] fields = null;
    public String[] internalLibraries = null;
    public String[] excludeLibraries = null;

    public Boolean autoDetect = true;
    public Boolean sort = true;
    public Comparator<Library> libraryComparator = null;

    public Boolean showLicense = false;
    public Boolean showLicenseDialog = true;
    public Boolean showVersion = false;
    public boolean showLoadingProgress = true;

    public Boolean aboutShowIcon = null;
    public String aboutVersionString = null;
    public String aboutAppName = null;
    public Boolean aboutShowVersion = null;
    public String aboutDescription = null;
    public Boolean aboutShowVersionName = false;
    public Boolean aboutShowVersionCode = false;

    public String aboutAppSpecial1 = null;
    public String aboutAppSpecial1Description = null;
    public String aboutAppSpecial2 = null;
    public String aboutAppSpecial2Description = null;
    public String aboutAppSpecial3 = null;
    public String aboutAppSpecial3Description = null;

    public Integer activityTheme = -1;
    public String activityTitle = null;
    public Colors activityColor = null;
    public Libs.ActivityStyle activityStyle = null;

    public LibTaskExecutor libTaskExecutor = LibTaskExecutor.DEFAULT_EXECUTOR;

    public HashMap<String, HashMap<String, String>> libraryModification = null;

    public Class ownLibsActivityClass = LibsActivity.class;

    public LibsBuilder() {
    }

    /**
     * Builder method to pass the an own LibsActivity.
     *
     * @param clazz Class
     * @return this
     */
    public LibsBuilder withOwnLibsActivityClass(@NonNull Class clazz) {
        this.ownLibsActivityClass = clazz;
        return this;
    }

    /**
     * Builder method to pass the R.string.class.getFields() array to the fragment/activity so we can also include all ressources which are within libraries or your app.
     *
     * @param fields R.string.class.getFields()
     * @return this
     */

    public LibsBuilder withFields(Field[] fields) {
        return withFields(Libs.toStringArray(fields));
    }

    /**
     * Builder method to pass the Libs.toStringArray(R.string.class.getFields()) array to the fragment/activity so we can also include all ressources which are within libraries or your app.
     *
     * @param fields Libs.toStringArray(R.string.class.getFields())
     * @return this
     */
    public LibsBuilder withFields(String... fields) {
        this.fields = fields;
        return this;
    }

    /**
     * Builder method to pass manual libraries (libs which are not autoDetected)
     *
     * @param libraries the identifiers of the manual added libraries
     * @return this
     */
    public LibsBuilder withLibraries(String... libraries) {
        this.internalLibraries = libraries;
        return this;
    }

    /**
     * Builder method to exclude specific libraries
     *
     * @param excludeLibraries the identifiers of the libraries which should be excluded
     * @return this
     */
    public LibsBuilder withExcludedLibraries(String... excludeLibraries) {
        this.excludeLibraries = excludeLibraries;
        return this;
    }

    /**
     * Builder method to disable autoDetect (default: enabled)
     *
     * @param autoDetect enabled or disabled
     * @return this
     */
    public LibsBuilder withAutoDetect(boolean autoDetect) {
        this.autoDetect = autoDetect;
        return this;
    }

    /**
     * Builder method to disable sort (default: enabled)
     *
     * @param sort enabled or disabled
     * @return this
     */
    public LibsBuilder withSortEnabled(boolean sort) {
        this.sort = sort;
        return this;
    }


    /**
     * Builder method to enable custom sorting of the libraries (default: null)
     *
     * @param libraryComparator comparator to customize the sorting of the libraries
     * @return this
     */
    public LibsBuilder withLibraryComparator(Comparator<Library> libraryComparator) {
        this.libraryComparator = libraryComparator;
        this.sort = (libraryComparator != null);
        return this;
    }

    /**
     * Builder method to enable the license display (default: disabled)
     *
     * @param showLicense enabled or disabled
     * @return this
     */
    public LibsBuilder withLicenseShown(boolean showLicense) {
        this.showLicense = showLicense;
        return this;
    }

    /**
     * Builder method to disable the license display as dialog (default: enabled)
     *
     * @param showLicenseDialog enabled or disabled
     * @return this
     */
    public LibsBuilder withLicenseDialog(boolean showLicenseDialog) {
        this.showLicenseDialog = showLicenseDialog;
        return this;
    }

    /**
     * Builder method to hide the version number (default: enabled)
     *
     * @param showVersion enabled or disabled
     * @return this
     */
    public LibsBuilder withVersionShown(boolean showVersion) {
        this.showVersion = showVersion;
        return this;
    }

    /**
     * Builder method to enable the display of the application icon as about this app view
     *
     * @param aboutShowIcon enabled or disabled
     * @return this
     */
    public LibsBuilder withAboutIconShown(boolean aboutShowIcon) {
        this.aboutShowIcon = aboutShowIcon;
        return this;
    }

    /**
     * Builder method to enable the display of the application version name and code as about this app view
     *
     * @param aboutShowVersion enabled or disabled
     * @return this
     */
    public LibsBuilder withAboutVersionShown(boolean aboutShowVersion) {
        this.aboutShowVersion = aboutShowVersion;
        this.aboutShowVersionName = aboutShowVersion;
        this.aboutShowVersionCode = aboutShowVersion;
        return this;
    }

    /**
     * Builder method to enable the display of the application version name as about this app view
     *
     * @param aboutShowVersion enabled or disabled
     * @return this
     */
    public LibsBuilder withAboutVersionShownName(boolean aboutShowVersion) {
        this.aboutShowVersionName = aboutShowVersion;
        return this;
    }

    /**
     * Builder method to enable the display of the application version code as about this app view
     *
     * @param aboutShowVersion enabled or disabled
     * @return this
     */
    public LibsBuilder withAboutVersionShownCode(boolean aboutShowVersion) {
        this.aboutShowVersionCode = aboutShowVersion;
        return this;
    }

    /**
     * Builder method to enable the display and set the text of the application version in the about this app view
     *
     * @param aboutVersionString enabled or disabled
     * @return this
     */
    public LibsBuilder withAboutVersionString(String aboutVersionString) {
        this.aboutVersionString = aboutVersionString;
        return this;
    }

    /**
     * Builder method to enable the display and set the text of the application name in the about this app view
     *
     * @param aboutAppName the name of this application
     * @return this
     */
    public LibsBuilder withAboutAppName(String aboutAppName) {
        this.aboutAppName = aboutAppName;
        return this;
    }

    /**
     * Builder method to enable the display and set the text of the application description as about this app view
     *
     * @param aboutDescription the description of this application
     * @return this
     */
    public LibsBuilder withAboutDescription(String aboutDescription) {
        this.aboutDescription = aboutDescription;
        return this;
    }

    /**
     * @param aboutAppSpecial1 the special button text
     * @return this
     */
    public LibsBuilder withAboutSpecial1(String aboutAppSpecial1) {
        this.aboutAppSpecial1 = aboutAppSpecial1;
        return this;
    }

    /**
     * @param aboutAppSpecial1Description the special dialog text
     * @return this
     */
    public LibsBuilder withAboutSpecial1Description(String aboutAppSpecial1Description) {
        this.aboutAppSpecial1Description = aboutAppSpecial1Description;
        return this;
    }

    /**
     * @param aboutAppSpecial2 the special button text
     * @return this
     */
    public LibsBuilder withAboutSpecial2(String aboutAppSpecial2) {
        this.aboutAppSpecial2 = aboutAppSpecial2;
        return this;
    }

    /**
     * @param aboutAppSpecial2Description the special dialog text
     * @return this
     */
    public LibsBuilder withAboutSpecial2Description(String aboutAppSpecial2Description) {
        this.aboutAppSpecial2Description = aboutAppSpecial2Description;
        return this;
    }

    /**
     * @param aboutAppSpecial3 the special button text
     * @return this
     */
    public LibsBuilder withAboutSpecial3(String aboutAppSpecial3) {
        this.aboutAppSpecial3 = aboutAppSpecial3;
        return this;
    }

    /**
     * @param aboutAppSpecial3Description the special dialog text
     * @return this
     */
    public LibsBuilder withAboutSpecial3Description(String aboutAppSpecial3Description) {
        this.aboutAppSpecial3Description = aboutAppSpecial3Description;
        return this;
    }

    /**
     * Builder method to set the activity theme
     *
     * @param activityTheme as example R.theme.AppTheme (just for the activity)
     * @return this
     */
    public LibsBuilder withActivityTheme(int activityTheme) {
        this.activityTheme = activityTheme;
        return this;
    }

    /**
     * Builder method to set the ActivityTitle
     *
     * @param activityTitle the activity title (just for the activity)
     * @return this
     */
    public LibsBuilder withActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
        return this;
    }

    /**
     * Builder method to set the ActivityColor
     *
     * @param activityColor the activity color (just for the activity)
     * @return this
     */
    public LibsBuilder withActivityColor(Colors activityColor) {
        this.activityColor = activityColor;
        return this;
    }

    /**
     * Builder method to set the ActivityStyle
     *
     * @param libraryStyle LibraryStyles.LIGHT / DARK / LIGHT_DARK_TOOLBAR
     * @return this
     */
    public LibsBuilder withActivityStyle(Libs.ActivityStyle libraryStyle) {
        this.activityStyle = libraryStyle;
        return this;
    }

    /**
     * Builder method to modify specific libraries. NOTE: This will overwrite any modifications with the helper methods
     *
     * @param libraryModification an HashMap identified by libraryID containing an HashMap with the modifications identified by elementID.
     * @return this
     */
    public LibsBuilder withLibraryModification(HashMap<String, HashMap<String, String>> libraryModification) {
        this.libraryModification = libraryModification;
        return this;
    }

    /**
     * Builder helper method to set modifications for specific libraries
     *
     * @param library           the library to be modified
     * @param modificationKey   the identifier for the specific modification
     * @param modificationValue the value for the specific modification
     * @return this
     */
    public LibsBuilder withLibraryModification(String library, Libs.LibraryFields modificationKey, String modificationValue) {
        if (this.libraryModification == null) {
            this.libraryModification = new HashMap<>();
        }

        if (!libraryModification.containsKey(library)) {
            libraryModification.put(library, new HashMap<String, String>());
        }

        libraryModification.get(library).put(modificationKey.name(), modificationValue);

        return this;
    }

    /**
     * Builder method to set the LibsListener for the AboutLibraries actions
     *
     * @param libsListener the listener to be notified
     * @return this
     */
    public LibsBuilder withListener(LibsConfiguration.LibsListener libsListener) {
        LibsConfiguration.getInstance().setListener(libsListener);
        return this;
    }

    /**
     * Builder method to set the LibsRecyclerViewListener for the AboutLibraries recyclerView elements
     *
     * @param recyclerViewListener
     * @return this
     */
    public LibsBuilder withLibsRecyclerViewListener(LibsConfiguration.LibsRecyclerViewListener recyclerViewListener) {
        LibsConfiguration.getInstance().setLibsRecyclerViewListener(recyclerViewListener);
        return this;
    }


    /**
     * Builder method to set the LibsUIListener for the AboutLibraries view to hook into the view creation
     *
     * @param uiListener
     * @return this
     */
    public LibsBuilder withUiListener(LibsConfiguration.LibsUIListener uiListener) {
        LibsConfiguration.getInstance().setUiListener(uiListener);
        return this;
    }

    /**
     * Builder method to set the LayoutAnimationController for the RecyclerView
     *
     * @param layoutAnimationController
     * @return this
     */
    public LibsBuilder withLayoutAnimationController(LayoutAnimationController layoutAnimationController) {
        LibsConfiguration.getInstance().setLayoutAnimationController(layoutAnimationController);
        return this;
    }

    /**
     * Builder method to define a custom Thread Executor for asynchronous operations
     *
     * @param libTaskExecutor
     * @return this
     */
    public LibsBuilder withLibTaskExecutor(LibTaskExecutor libTaskExecutor) {
        if (libTaskExecutor != null) {
            this.libTaskExecutor = libTaskExecutor;
        }
        return this;
    }

    /**
     * Builder method to define a custom callback which is invoked every time the LibraryTask gets executed.
     * This interface is called on a LibraryTask's start and end. Make sure the class which implements the
     * LibTaskCallback is Serializable.
     *
     * @param libTaskCallback
     * @return this
     */
    public LibsBuilder withLibTaskCallback(LibTaskCallback libTaskCallback) {
        LibsConfiguration.getInstance().setLibTaskCallback(libTaskCallback);
        return this;
    }

    /**
     * Builder method to allow you to disable the automatically shown loading progressBar while the libraries are loading
     *
     * @param showLoadingProgress
     * @return this
     */
    public LibsBuilder withShowLoadingProgress(boolean showLoadingProgress) {
        this.showLoadingProgress = showLoadingProgress;
        return this;
    }

    /*
     * START OF THE FINAL METHODS
     */


    private void preCheck() {
        if (fields == null) {
            Log.w("AboutLibraries", "Have you missed to call withFields(R.string.class.getFields())? - autoDetect won't work - https://github.com/mikepenz/AboutLibraries/wiki/HOWTO:-Fragment");
        }
    }

    /**
     * builder to build an adapter out of the given information ;D
     *
     * @param context the current context
     * @return a LibsRecyclerViewAdapter with the libraries
     */
    public FastAdapter adapter(Context context) {
        Libs libs;
        if (fields == null) {
            libs = new Libs(context);
        } else {
            libs = new Libs(context, fields);
        }

        //apply modifications
        libs.modifyLibraries(libraryModification);

        //fetch the libraries and sort if a comparator was set
        ArrayList<Library> libraries = libs.prepareLibraries(context, internalLibraries, excludeLibraries, autoDetect, sort);

        //prepare adapter
        ItemAdapter itemAdapter = new ItemAdapter();
        List<LibraryItem> libraryItems = new ArrayList<>();
        for (Library library : libraries) {
            libraryItems.add(new LibraryItem().withLibrary(library).withLibsBuilder(this));
        }

        FastAdapter fastAdapter = FastAdapter.with(itemAdapter);
        //noinspection unchecked
        itemAdapter.add(libraryItems);

        return fastAdapter;
    }

    /**
     * intent() method to build and create the intent with the set params
     *
     * @return the intent to start the activity
     */
    public Intent intent(Context ctx) {
        return intent(ctx, ownLibsActivityClass);
    }

    /**
     * intent() method to build and create the intent with the set params
     *
     * @return the intent to start the activity
     */
    public Intent intent(Context ctx, Class clazz) {
        preCheck();

        Intent i = new Intent(ctx, clazz);
        i.putExtra("data", this);
        i.putExtra(Libs.BUNDLE_THEME, this.activityTheme);

        if (this.activityTitle != null) {
            i.putExtra(Libs.BUNDLE_TITLE, this.activityTitle);
        }

        if (this.activityColor != null) {
            i.putExtra(Libs.BUNDLE_COLORS, this.activityColor);
        }

        if (this.activityStyle != null) {
            i.putExtra(Libs.BUNDLE_STYLE, this.activityStyle.name());
        }

        return i;
    }

    /**
     * start() method to start the application
     */
    public void start(Context ctx) {
        Intent i = intent(ctx);
        ctx.startActivity(i);
    }

    /**
     * activity() method to start the application
     */
    public void activity(Context ctx) {
        start(ctx);
    }

    /**
     * supportFragment() method to build and create the fragment with the set params
     *
     * @return the fragment to set in your application
     */
    public LibsSupportFragment supportFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", this);

        LibsSupportFragment fragment = new LibsSupportFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    /**
     * supportFragment() method to build and create the fragment with the set params
     *
     * @return the fragment to set in your application
     */
    public LibsFragment fragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", this);

        LibsFragment fragment = new LibsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }
}
