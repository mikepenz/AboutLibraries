@file:JvmName("LibsBuilder")
@file:Suppress("DEPRECATION")

package com.mikepenz.aboutlibraries

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.LibsActivity
import com.mikepenz.aboutlibraries.ui.LibsSupportFragment
import com.mikepenz.aboutlibraries.util.SerializableLibs
import com.mikepenz.aboutlibraries.util.toLibs
import com.mikepenz.aboutlibraries.util.toSerializable
import java.io.Serializable

@Deprecated("The legacy view based UI will be deprecated in the future. Please consider moving to the compose based UI.")
class LibsBuilder : Serializable {
    var sort: Boolean = true
    var libraryComparator: Comparator<Library>? = null

    @Suppress("VariableNaming")
    internal var _showLicense: Boolean? = null
    var showLicense: Boolean = true
        set(value) {
            _showLicense = value
            field = value
        }
    var showLicenseDialog: Boolean = true

    @Suppress("VariableNaming")
    internal var _showVersion: Boolean? = null
    var showVersion: Boolean = true
        set(value) {
            _showVersion = value
            field = value
        }
    var showLoadingProgress = true

    @Suppress("VariableNaming")
    internal var _aboutShowIcon: Boolean? = null
    var aboutShowIcon: Boolean = true
        set(value) {
            _aboutShowIcon = value
            field = value
        }
    var aboutVersionString: String = ""
    var aboutAppName: String? = null

    @Suppress("VariableNaming")
    internal var _aboutShowVersion: Boolean? = null
    var aboutShowVersion: Boolean = true
        set(value) {
            _aboutShowVersion = value
            field = value
        }
    var aboutDescription: String? = null

    @Suppress("VariableNaming")
    internal var _aboutShowVersionName: Boolean? = null
    var aboutShowVersionName: Boolean = true
        set(value) {
            _aboutShowVersionName = value
            field = value
        }

    @Suppress("VariableNaming")
    internal var _aboutShowVersionCode: Boolean? = null
    var aboutShowVersionCode: Boolean = true
        set(value) {
            _aboutShowVersionCode = value
            field = value
        }

    internal var _libs: SerializableLibs? = null

    @Transient
    var libs: Libs? = null
        get() = field ?: _libs?.toLibs()
        set(value) {
            _libs = value?.toSerializable()
        }

    var aboutMinimalDesign: Boolean = false

    var aboutAppSpecial1: String? = null
    var aboutAppSpecial1Description: String? = null
    var aboutAppSpecial2: String? = null
    var aboutAppSpecial2Description: String? = null
    var aboutAppSpecial3: String? = null
    var aboutAppSpecial3Description: String? = null

    var activityTitle: String? = null
    var edgeToEdge: Boolean = false
    var searchEnabled: Boolean = false

    /**
     * Builder method to provide the library information via libs.
     * This call is optional. By default the default definitions are loaded.
     *
     * @param libs the [Libs] entity with the library and license information
     * @return this
     */
    fun withLibs(libs: Libs): LibsBuilder {
        this.libs = libs
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
     * Builder method to show the list in a minimal design
     *
     * @param aboutMinimalDesign enabled or disabled
     * @return this
     */
    fun withAboutMinimalDesign(aboutMinimalDesign: Boolean): LibsBuilder {
        this.aboutMinimalDesign = aboutMinimalDesign
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
     * Builder method to set the view to be edge to edge
     *
     * @param asEdgeToEdge true / false
     * @return this
     */
    fun withEdgeToEdge(asEdgeToEdge: Boolean): LibsBuilder {
        this.edgeToEdge = asEdgeToEdge
        return this
    }

    /**
     * Builder method to set the LibsListener for the AboutLibraries actions
     *
     * @param libsListener the listener to be notified
     * @return this
     */
    fun withListener(libsListener: LibsConfiguration.LibsListener): LibsBuilder {
        LibsConfiguration.listener = libsListener
        return this
    }

    /**
     * Builder method to set the LibsUIListener for the AboutLibraries view to hook into the view creation
     *
     * @param uiListener
     * @return this
     */
    fun withUiListener(uiListener: LibsConfiguration.LibsUIListener): LibsBuilder {
        LibsConfiguration.uiListener = uiListener
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

    /**
     * Builder method to allow you to toggle search in [LibsActivity], which will be displayed using
     * a standard SearchView in the Toolbar. If you are directly using [LibsSupportFragment] you'll
     * need to implement + hook up your search UI to [LibsSupportFragment.getFilter]
     *
     * @param searchEnabled
     * @return this
     */
    fun withSearchEnabled(searchEnabled: Boolean): LibsBuilder {
        this.searchEnabled = searchEnabled
        return this
    }


    /**
     * intent() method to build and create the intent with the set params
     *
     * @return the intent to start the activity
     */
    fun intent(ctx: Context): Intent {
        val i = Intent(ctx, LibsActivity::class.java)
        i.putExtra("data", this)

        if (this.activityTitle != null) {
            i.putExtra(BUNDLE_TITLE, this.activityTitle)
        }
        i.putExtra(BUNDLE_EDGE_TO_EDGE, this.edgeToEdge)
        i.putExtra(BUNDLE_SEARCH_ENABLED, this.searchEnabled)

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

    companion object {
        const val BUNDLE_TITLE = "ABOUT_LIBRARIES_TITLE"
        const val BUNDLE_EDGE_TO_EDGE = "ABOUT_LIBRARIES_EDGE_TO_EDGE"
        const val BUNDLE_SEARCH_ENABLED = "ABOUT_LIBRARIES_SEARCH_ENABLED"
    }
}