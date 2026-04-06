@file:Suppress("DEPRECATION")

package com.mikepenz.aboutlibraries.viewmodel

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.item.HeaderItem
import com.mikepenz.aboutlibraries.ui.item.LibraryItem
import com.mikepenz.aboutlibraries.ui.item.LoaderItem
import com.mikepenz.aboutlibraries.ui.item.SimpleLibraryItem
import com.mikepenz.aboutlibraries.util.extractBooleanBundleOrResource
import com.mikepenz.aboutlibraries.util.extractStringBundleOrResource
import com.mikepenz.fastadapter.GenericItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class LibsViewModel(
    // TODO replace with non ctx solution?
    @Suppress("StaticFieldLeak") private val ctx: Context,
    internal val builder: LibsBuilder, // ui module
    private val libsBuilder: Libs.Builder,
) : ViewModel() {

    private var versionName: String? = null
    private var versionCode: Long? = null

    init {
        //fill the builder with the information
        builder.showLicense = ctx.extractBooleanBundleOrResource(builder._showLicense, "aboutLibraries_showLicense") ?: true
        builder.showVersion = ctx.extractBooleanBundleOrResource(builder._showVersion, "aboutLibraries_showVersion") ?: true

        builder.aboutShowIcon = ctx.extractBooleanBundleOrResource(builder._aboutShowIcon, "aboutLibraries_description_showIcon") ?: false
        builder.aboutShowVersion = ctx.extractBooleanBundleOrResource(builder._aboutShowVersion, "aboutLibraries_description_showVersion") ?: false
        builder.aboutShowVersionName = ctx.extractBooleanBundleOrResource(
            builder._aboutShowVersionName, "aboutLibraries_description_showVersionName"
        ) ?: false
        builder.aboutShowVersionCode = ctx.extractBooleanBundleOrResource(
            builder._aboutShowVersionCode, "aboutLibraries_description_showVersionCode"
        ) ?: false

        builder.aboutAppName = ctx.extractStringBundleOrResource(builder.aboutAppName, "aboutLibraries_description_name") ?: ""
        builder.aboutDescription = ctx.extractStringBundleOrResource(builder.aboutDescription, "aboutLibraries_description_text") ?: ""

        builder.aboutAppSpecial1 = ctx.extractStringBundleOrResource(builder.aboutAppSpecial1, "aboutLibraries_description_special1_name")
        builder.aboutAppSpecial1Description = ctx.extractStringBundleOrResource(
            builder.aboutAppSpecial1Description, "aboutLibraries_description_special1_text"
        )
        builder.aboutAppSpecial2 = ctx.extractStringBundleOrResource(builder.aboutAppSpecial2, "aboutLibraries_description_special2_name")
        builder.aboutAppSpecial2Description = ctx.extractStringBundleOrResource(
            builder.aboutAppSpecial2Description, "aboutLibraries_description_special2_text"
        )
        builder.aboutAppSpecial3 = ctx.extractStringBundleOrResource(builder.aboutAppSpecial3, "aboutLibraries_description_special3_name")
        builder.aboutAppSpecial3Description = ctx.extractStringBundleOrResource(
            builder.aboutAppSpecial3Description, "aboutLibraries_description_special3_text"
        )


        //load the data for the header
        val showVersionInfo = builder.aboutShowVersion || builder.aboutShowVersionName || builder.aboutShowVersionCode
        if (builder.aboutShowIcon && showVersionInfo) {
            //get the packageManager to load and read some values :D
            val pm = ctx.packageManager
            //get the packageName
            val packageName = ctx.packageName
            //Try to load the applicationInfo
            val packageInfo: PackageInfo? = try {
                pm.getPackageInfo(packageName, 0)
            } catch (ignored: Exception) {
                null
            }

            //set the Version or hide it
            if (packageInfo != null) {
                versionName = packageInfo.versionName
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    versionCode = packageInfo.longVersionCode
                } else {
                    @Suppress("DEPRECATION")
                    versionCode = packageInfo.versionCode.toLong()
                }
            }
        }
    }

    val listItems = flow {
        if (builder.showLoadingProgress) {
            emit(listOf(LoaderItem()))
        }

        withContext(Dispatchers.IO) {
            val builtLibs = try {
                builder.libs ?: libsBuilder.build()
            } catch (t: Throwable) {
                Log.e("AboutLibraries", "Unable to read the library information", t)
                withContext(Dispatchers.Main) {
                    emit(emptyList<GenericItem>())
                }
                return@withContext
            }

            val comparator = builder.libraryComparator
            val libraries: Iterable<Library> = if (comparator != null) {
                builtLibs.libraries.sortedWith(comparator)
            } else {
                builtLibs.libraries
            }

            val finalList = mutableListOf<GenericItem>()
            //Add the header
            val icon = try {
                ctx.packageManager.getApplicationInfo(ctx.packageName, 0)
            } catch (ignored: Exception) {
                null
            }?.loadIcon(ctx.packageManager)

            val showVersionInfo = builder.aboutShowVersion || builder.aboutShowVersionName || builder.aboutShowVersionCode
            if (builder.aboutShowIcon && showVersionInfo) {
                //add this cool thing to the headerView of our listView
                finalList.add(
                    HeaderItem(builder).withAboutVersionName(versionName).withAboutVersionCode(versionCode).withAboutIcon(icon)
                )
            }

            //add the libs
            for (library in libraries) {
                when {
                    builder.aboutMinimalDesign -> finalList.add(SimpleLibraryItem(library, builder))
                    else -> finalList.add(LibraryItem(library, builder))
                }
            }

            withContext(Dispatchers.Main) {
                emit(finalList)
            }
        }
    }
}