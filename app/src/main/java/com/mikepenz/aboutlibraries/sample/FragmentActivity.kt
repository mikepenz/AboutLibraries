package com.mikepenz.aboutlibraries.sample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.mikepenz.aboutlibraries.LibTaskCallback
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.LibsConfiguration
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem

/**
 * Created by mikepenz on 04.06.14.
 */
class FragmentActivity : AppCompatActivity() {

    internal var libTaskCallback: LibTaskCallback = object : LibTaskCallback {
        override fun onLibTaskStarted() {
            Log.e("AboutLibraries", "started")
        }

        override fun onLibTaskFinished(fastItemAdapter: ItemAdapter<*>) {
            Log.e("AboutLibraries", "finished")
        }
    }

    internal var libsUIListener: LibsConfiguration.LibsUIListener = object : LibsConfiguration.LibsUIListener {
        override fun preOnCreateView(view: View): View {
            return view
        }

        override fun postOnCreateView(view: View): View {
            return view
        }
    }

    internal var libsListener: LibsConfiguration.LibsListener = object : LibsConfiguration.LibsListener {
        override fun onIconClicked(v: View) {
            Toast.makeText(v.context, "We are able to track this now ;)", Toast.LENGTH_LONG).show()
        }

        override fun onLibraryAuthorClicked(v: View, library: Library): Boolean {
            return false
        }

        override fun onLibraryContentClicked(v: View, library: Library): Boolean {
            return false
        }

        override fun onLibraryBottomClicked(v: View, library: Library): Boolean {
            return false
        }

        override fun onExtraClicked(v: View, specialButton: Libs.SpecialButton): Boolean {
            return false
        }

        override fun onIconLongClicked(v: View): Boolean {
            return false
        }

        override fun onLibraryAuthorLongClicked(v: View, library: Library): Boolean {
            return false
        }

        override fun onLibraryContentLongClicked(v: View, library: Library): Boolean {
            return false
        }

        override fun onLibraryBottomLongClicked(v: View, library: Library): Boolean {
            return false
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        //Remove line to test RTL support
        //window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL

        // Handle Toolbar
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        DrawerBuilder(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        PrimaryDrawerItem().withName("Home"),
                        PrimaryDrawerItem().withName(R.string.action_manifestactivity).withIdentifier(R.id.action_manifestactivity.toLong()).withSelectable(false),
                        PrimaryDrawerItem().withName(R.string.action_extendactivity).withIdentifier(R.id.action_extendactivity.toLong()).withSelectable(false),
                        PrimaryDrawerItem().withName(R.string.action_customsortactivity).withIdentifier(R.id.action_customsortactivity.toLong()).withSelectable(false),
                        PrimaryDrawerItem().withName(R.string.action_opensource).withIdentifier(R.id.action_opensource.toLong()).withSelectable(false)
                )
                .withOnDrawerItemClickListener { _, _, drawerItem ->
                    // Handle action bar item clicks here. The action bar will
                    // automatically handle clicks on the Home/Up button, so long
                    // as you specify a parent activity in AndroidManifest.xml.

                    val id = drawerItem.identifier
                    if (id == R.id.action_opensource.toLong()) {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mikepenz/AboutLibraries"))
                        startActivity(browserIntent)
                    } else if (id == R.id.action_extendactivity.toLong()) {
                        val intent = Intent(applicationContext, ExtendActivity::class.java)
                        startActivity(intent)
                    } else if (id == R.id.action_customsortactivity.toLong()) {
                        val intent = Intent(applicationContext, CustomSortActivity::class.java)
                        startActivity(intent)
                    } else if (id == R.id.action_manifestactivity.toLong()) {
                        LibsBuilder()
                                .withLibraries("crouton", "actionbarsherlock", "showcaseview", "glide")
                                .withAutoDetect(false)
                                .withLicenseShown(true)
                                .withVersionShown(true)
                                .withActivityTitle("Open Source")
                                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                                .withListener(libsListener)
                                .withLibTaskCallback(libTaskCallback)
                                .withUiListener(libsUIListener)
                                .start(this@FragmentActivity)
                    }

                    false
                }
                .build()


        /*
        //NOTE: This is how you can modify a specific library definition during runtime
        HashMap<String, HashMap<String, String>> libsModification = new HashMap<String, HashMap<String, String>>();
        HashMap<String, String> modifyAboutLibraries = new HashMap<String, String>();
        modifyAboutLibraries.put("name", "_AboutLibraries");
        libsModification.put("aboutlibraries", modifyAboutLibraries);
        .withLibraryModification(libsModification);
        */

        val fragment = LibsBuilder()
                .withVersionShown(false)
                .withLicenseShown(true)
                .withLibraryModification("aboutlibraries", Libs.LibraryFields.LIBRARY_NAME, "_AboutLibraries")
                .supportFragment()

        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()

    }
}
