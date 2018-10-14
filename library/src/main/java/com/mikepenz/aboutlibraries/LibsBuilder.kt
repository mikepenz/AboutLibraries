@file:JvmName("LibsBuilder")

package com.mikepenz.aboutlibraries

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.LayoutAnimationController
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.LibsActivity
import com.mikepenz.aboutlibraries.ui.LibsFragment
import com.mikepenz.aboutlibraries.ui.LibsSupportFragment
import com.mikepenz.aboutlibraries.ui.item.LibraryItem
import com.mikepenz.aboutlibraries.util.Colors
import com.mikepenz.aboutlibraries.util.toStringArray
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import java.io.Serializable
import java.lang.reflect.Field
import java.util.*
import kotlin.collections.HashMap

class LibsBuilder : Serializable {
    var fields: Array<String> = emptyArray()
    var internalLibraries: Array<out String> = emptyArray()
    var excludeLibraries: Array<out String> = emptyArray()

    var autoDetect: Boolean = true
    var checkCachedDetection: Boolean = true
    var sort: Boolean = true
    var libraryComparator: Comparator<Library>? = null

    var showLicense: Boolean = false
    var showLicenseDialog: Boolean = true
    var showVersion: Boolean = true
    var showLoadingProgress = true

    var aboutShowIcon: Boolean = true
    var aboutVersionString: String = ""
    var aboutAppName: String = ""
    var aboutShowVersion: Boolean = true
    var aboutDescription: String = ""
    var aboutShowVersionName: Boolean = true
    var aboutShowVersionCode: Boolean = true

    var aboutAppSpecial1: String? = null
    var aboutAppSpecial1Description: String? = null
    var aboutAppSpecial2: String? = null
    var aboutAppSpecial2Description: String? = null
    var aboutAppSpecial3: String? = null
    var aboutAppSpecial3Description: String? = null

    var activityTheme: Int = -1
    var activityTitle: String? = null
    var activityColor: Colors? = null
    var activityStyle: Libs.ActivityStyle? = null

    var libTaskExecutor = LibTaskExecutor.DEFAULT_EXECUTOR

    val libraryModification: HashMap<String, HashMap<String, String>> = HashMap()

    var ownLibsActivityClass: Class<*> = LibsActivity::class.java

    /**
     * Builder method to pass the an own LibsActivity.
     *
     * @param clazz Class
     * @return this
     */
    fun withOwnLibsActivityClass(clazz: Class<*>): LibsBuilder {
        this.ownLibsActivityClass = clazz
        return this
    }

    /**
     * Builder method to pass the R.string.class.getFields() array to the fragment/activity so we can also include all ressources which are within libraries or your app.
     *
     * @param fields R.string.class.getFields()
     * @return this
     */

    fun withFields(fields: Array<Field>): LibsBuilder {
        return withFields(fields.toStringArray())
    }

    /**
     * Builder method to pass the Libs.toStringArray(R.string.class.getFields()) array to the fragment/activity so we can also include all ressources which are within libraries or your app.
     *
     * @param fields Libs.toStringArray(R.string.class.getFields())
     * @return this
     */
    fun withFields(fields: Array<String>): LibsBuilder {
        this.fields = fields
        return this
    }

    /**
     * Builder method to pass manual libraries (libs which are not autoDetected)
     *
     * @param libraries the identifiers of the manual added libraries
     * @return this
     */
    fun withLibraries(vararg libraries: String): LibsBuilder {
        this.internalLibraries = libraries
        return this
    }

    /**
     * Builder method to exclude specific libraries
     *
     * @param excludeLibraries the identifiers of the libraries which should be excluded
     * @return this
     */
    fun withExcludedLibraries(vararg excludeLibraries: String): LibsBuilder {
        this.excludeLibraries = excludeLibraries
        return this
    }

    /**
     * Builder method to disable autoDetect (default: enabled)
     *
     * @param autoDetect enabled or disabled
     * @return this
     */
    fun withAutoDetect(autoDetect: Boolean): LibsBuilder {
        this.autoDetect = autoDetect
        return this
    }

    /**
     * Builder method to disable checking the cached autodetected libraries (per version) (default: enabled)
     *
     * @param checkCachedDetection enabled or disabled
     * @return this
     */
    fun withCheckCachedDetection(checkCachedDetection: Boolean): LibsBuilder {
        this.checkCachedDetection = checkCachedDetection
        return this
    }

    /**
     * Builder method to disable sort (default: enabled)
     *
     * @param sort enabled or disabled
     * @return this
     */
    fun withSortEnabled(sort: Boolean): LibsBuilder {
        this.sort = sort
        return this
    }


    /**
     * Builder method to enable custom sorting of the libraries (default: null)
     *
     * @param libraryComparator comparator to customize the sorting of the libraries
     * @return this
     */
    fun withLibraryComparator(libraryComparator: Comparator<Library>?): LibsBuilder {
        this.libraryComparator = libraryComparator
        this.sort = libraryComparator != null
        return this
    }

    /**
     * Builder method to enable the license display (default: disabled)
     *
     * @param showLicense enabled or disabled
     * @return this
     */
    fun withLicenseShown(showLicense: Boolean): LibsBuilder {
        this.showLicense = showLicense
        return this
    }

    /**
     * Builder method to disable the license display as dialog (default: enabled)
     *
     * @param showLicenseDialog enabled or disabled
     * @return this
     */
    fun withLicenseDialog(showLicenseDialog: Boolean): LibsBuilder {
        this.showLicenseDialog = showLicenseDialog
        return this
    }

    /**
     * Builder method to hide the version number (default: enabled)
     *
     * @param showVersion enabled or disabled
     * @return this
     */
    fun withVersionShown(showVersion: Boolean): LibsBuilder {
        this.showVersion = showVersion
        return this
    }

    /**
     * Builder method to enable the display of the application icon as about this app view
     *
     * @param aboutShowIcon enabled or disabled
     * @return this
     */
    fun withAboutIconShown(aboutShowIcon: Boolean): LibsBuilder {
        this.aboutShowIcon = aboutShowIcon
        return this
    }

    /**
     * Builder method to enable the display of the application version name and code as about this app view
     *
     * @param aboutShowVersion enabled or disabled
     * @return this
     */
    fun withAboutVersionShown(aboutShowVersion: Boolean): LibsBuilder {
        this.aboutShowVersion = aboutShowVersion
        this.aboutShowVersionName = aboutShowVersion
        this.aboutShowVersionCode = aboutShowVersion
        return this
    }

    /**
     * Builder method to enable the display of the application version name as about this app view
     *
     * @param aboutShowVersion enabled or disabled
     * @return this
     */
    fun withAboutVersionShownName(aboutShowVersion: Boolean): LibsBuilder {
        this.aboutShowVersionName = aboutShowVersion
        return this
    }

    /**
     * Builder method to enable the display of the application version code as about this app view
     *
     * @param aboutShowVersion enabled or disabled
     * @return this
     */
    fun withAboutVersionShownCode(aboutShowVersion: Boolean): LibsBuilder {
        this.aboutShowVersionCode = aboutShowVersion
        return this
    }

    /**
     * Builder method to enable the display and set the text of the application version in the about this app view
     *
     * @param aboutVersionString enabled or disabled
     * @return this
     */
    fun withAboutVersionString(aboutVersionString: String): LibsBuilder {
        this.aboutVersionString = aboutVersionString
        return this
    }

    /**
     * Builder method to enable the display and set the text of the application name in the about this app view
     *
     * @param aboutAppName the name of this application
     * @return this
     */
    fun withAboutAppName(aboutAppName: String): LibsBuilder {
        this.aboutAppName = aboutAppName
        return this
    }

    /**
     * Builder method to enable the display and set the text of the application description as about this app view
     *
     * @param aboutDescription the description of this application
     * @return this
     */
    fun withAboutDescription(aboutDescription: String): LibsBuilder {
        this.aboutDescription = aboutDescription
        return this
    }

    /**
     * @param aboutAppSpecial1 the special button text
     * @return this
     */
    fun withAboutSpecial1(aboutAppSpecial1: String): LibsBuilder {
        this.aboutAppSpecial1 = aboutAppSpecial1
        return this
    }

    /**
     * @param aboutAppSpecial1Description the special dialog text
     * @return this
     */
    fun withAboutSpecial1Description(aboutAppSpecial1Description: String): LibsBuilder {
        this.aboutAppSpecial1Description = aboutAppSpecial1Description
        return this
    }

    /**
     * @param aboutAppSpecial2 the special button text
     * @return this
     */
    fun withAboutSpecial2(aboutAppSpecial2: String): LibsBuilder {
        this.aboutAppSpecial2 = aboutAppSpecial2
        return this
    }

    /**
     * @param aboutAppSpecial2Description the special dialog text
     * @return this
     */
    fun withAboutSpecial2Description(aboutAppSpecial2Description: String): LibsBuilder {
        this.aboutAppSpecial2Description = aboutAppSpecial2Description
        return this
    }

    /**
     * @param aboutAppSpecial3 the special button text
     * @return this
     */
    fun withAboutSpecial3(aboutAppSpecial3: String): LibsBuilder {
        this.aboutAppSpecial3 = aboutAppSpecial3
        return this
    }

    /**
     * @param aboutAppSpecial3Description the special dialog text
     * @return this
     */
    fun withAboutSpecial3Description(aboutAppSpecial3Description: String): LibsBuilder {
        this.aboutAppSpecial3Description = aboutAppSpecial3Description
        return this
    }

    /**
     * Builder method to set the activity theme
     *
     * @param activityTheme as example R.theme.AppTheme (just for the activity)
     * @return this
     */
    fun withActivityTheme(activityTheme: Int): LibsBuilder {
        this.activityTheme = activityTheme
        return this
    }

    /**
     * Builder method to set the ActivityTitle
     *
     * @param activityTitle the activity title (just for the activity)
     * @return this
     */
    fun withActivityTitle(activityTitle: String): LibsBuilder {
        this.activityTitle = activityTitle
        return this
    }

    /**
     * Builder method to set the ActivityColor
     *
     * @param activityColor the activity color (just for the activity)
     * @return this
     */
    fun withActivityColor(activityColor: Colors): LibsBuilder {
        this.activityColor = activityColor
        return this
    }

    /**
     * Builder method to set the ActivityStyle
     *
     * @param libraryStyle LibraryStyles.LIGHT / DARK / LIGHT_DARK_TOOLBAR
     * @return this
     */
    fun withActivityStyle(libraryStyle: Libs.ActivityStyle): LibsBuilder {
        this.activityStyle = libraryStyle
        return this
    }

    /**
     * Builder method to modify specific libraries. NOTE: This will overwrite any modifications with the helper methods
     *
     * @param libraryModification an HashMap identified by libraryID containing an HashMap with the modifications identified by elementID.
     * @return this
     */
    fun withLibraryModification(libraryModification: HashMap<String, HashMap<String, String>>): LibsBuilder {
        this.libraryModification.clear()
        this.libraryModification.putAll(libraryModification)
        return this
    }

    /**
     * Builder helper method to set modifications for specific libraries
     *
     * @param library           the library to be modified
     * @param modificationKey   the identifier for the specific modification
     * @param modificationValue the value for the specific modification
     * @return this
     */
    fun withLibraryModification(library: String, modificationKey: Libs.LibraryFields, modificationValue: String): LibsBuilder {
        if (!libraryModification.containsKey(library)) {
            libraryModification[library] = HashMap()
        }
        libraryModification[library]?.set(modificationKey.name, modificationValue)

        return this
    }

    /**
     * Builder method to set the LibsListener for the AboutLibraries actions
     *
     * @param libsListener the listener to be notified
     * @return this
     */
    fun withListener(libsListener: LibsConfiguration.LibsListener): LibsBuilder {
        LibsConfiguration.instance.listener = libsListener
        return this
    }

    /**
     * Builder method to set the LibsRecyclerViewListener for the AboutLibraries recyclerView elements
     *
     * @param recyclerViewListener
     * @return this
     */
    fun withLibsRecyclerViewListener(recyclerViewListener: LibsConfiguration.LibsRecyclerViewListener): LibsBuilder {
        LibsConfiguration.instance.libsRecyclerViewListener = recyclerViewListener
        return this
    }


    /**
     * Builder method to set the LibsUIListener for the AboutLibraries view to hook into the view creation
     *
     * @param uiListener
     * @return this
     */
    fun withUiListener(uiListener: LibsConfiguration.LibsUIListener): LibsBuilder {
        LibsConfiguration.instance.uiListener = uiListener
        return this
    }

    /**
     * Builder method to set the LayoutAnimationController for the RecyclerView
     *
     * @param layoutAnimationController
     * @return this
     */
    fun withLayoutAnimationController(layoutAnimationController: LayoutAnimationController): LibsBuilder {
        LibsConfiguration.instance.layoutAnimationController = layoutAnimationController
        return this
    }

    /**
     * Builder method to define a custom Thread Executor for asynchronous operations
     *
     * @param libTaskExecutor
     * @return this
     */
    fun withLibTaskExecutor(libTaskExecutor: LibTaskExecutor?): LibsBuilder {
        if (libTaskExecutor != null) {
            this.libTaskExecutor = libTaskExecutor
        }
        return this
    }

    /**
     * Builder method to define a custom callback which is invoked every time the LibraryTask gets executed.
     * This interface is called on a LibraryTask's start and end. Make sure the class which implements the
     * LibTaskCallback is Serializable.
     *
     * @param libTaskCallback
     * @return this
     */
    fun withLibTaskCallback(libTaskCallback: LibTaskCallback): LibsBuilder {
        LibsConfiguration.instance.libTaskCallback = libTaskCallback
        return this
    }

    /**
     * Builder method to allow you to disable the automatically shown loading progressBar while the libraries are loading
     *
     * @param showLoadingProgress
     * @return this
     */
    fun withShowLoadingProgress(showLoadingProgress: Boolean): LibsBuilder {
        this.showLoadingProgress = showLoadingProgress
        return this
    }

    /*
     * START OF THE FINAL METHODS
     */


    private fun preCheck() {
        if (fields.isEmpty()) {
            Log.w("AboutLibraries", "Have you missed to call withFields(R.string.class.getFields())? - autoDetect won't work - https://github.com/mikepenz/AboutLibraries/wiki/HOWTO:-Fragment")
        }
    }

    /**
     * builder to build an adapter out of the given information ;D
     *
     * @param context the current context
     * @return a LibsRecyclerViewAdapter with the libraries
     */
    fun adapter(context: Context): FastAdapter<*> {
        val libs: Libs = if (fields.isEmpty()) {
            Libs(context)
        } else {
            Libs(context, fields)
        }

        //apply modifications
        libs.modifyLibraries(libraryModification)

        //fetch the libraries and sort if a comparator was set
        val libraries = libs.prepareLibraries(context, internalLibraries, excludeLibraries, autoDetect, checkCachedDetection, sort)

        //prepare adapter
        val itemAdapter = ItemAdapter<IItem<*, *>>()
        val libraryItems = ArrayList<IItem<*, *>>()
        for (library in libraries) {
            libraryItems.add(LibraryItem(library, this))
        }

        val fastAdapter = FastAdapter.with<IItem<*, *>, ItemAdapter<*>>(itemAdapter)

        itemAdapter.add(libraryItems)

        return fastAdapter
    }

    /**
     * intent() method to build and create the intent with the set params
     *
     * @return the intent to start the activity
     */
    @JvmOverloads
    fun intent(ctx: Context, clazz: Class<*> = ownLibsActivityClass): Intent {
        preCheck()

        val i = Intent(ctx, clazz)
        i.putExtra("data", this)
        i.putExtra(Libs.BUNDLE_THEME, this.activityTheme)

        if (this.activityTitle != null) {
            i.putExtra(Libs.BUNDLE_TITLE, this.activityTitle)
        }

        if (this.activityColor != null) {
            i.putExtra(Libs.BUNDLE_COLORS, this.activityColor)
        }

        if (this.activityStyle != null) {
            i.putExtra(Libs.BUNDLE_STYLE, this.activityStyle?.name)
        }

        return i
    }

    /**
     * start() method to start the application
     */
    fun start(ctx: Context) {
        val i = intent(ctx)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(i)
    }

    /**
     * activity() method to start the application
     */
    fun activity(ctx: Context) {
        start(ctx)
    }

    /**
     * supportFragment() method to build and create the fragment with the set params
     *
     * @return the fragment to set in your application
     */
    fun supportFragment(): LibsSupportFragment {
        if (libraryComparator != null) {
            throw IllegalArgumentException("Can not use a 'libraryComparator' with the support fragment")
        }

        val bundle = Bundle()
        bundle.putSerializable("data", this)

        val fragment = LibsSupportFragment()
        fragment.arguments = bundle

        return fragment
    }

    /**
     * supportFragment() method to build and create the fragment with the set params
     *
     * @return the fragment to set in your application
     */
    fun fragment(): LibsFragment {
        if (libraryComparator != null) {
            throw IllegalArgumentException("Can not use a 'libraryComparator' with the fragment")
        }

        val bundle = Bundle()
        bundle.putSerializable("data", this)

        val fragment = LibsFragment()
        fragment.arguments = bundle

        return fragment
    }
}
/**
 * intent() method to build and create the intent with the set params
 *
 * @return the intent to start the activity
 */
