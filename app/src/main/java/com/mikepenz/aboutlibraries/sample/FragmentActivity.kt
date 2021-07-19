package com.mikepenz.aboutlibraries.sample

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.mikepenz.aboutlibraries.LibTaskCallback
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.LibsConfiguration
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.sample.databinding.ActivityFragmentBinding
import com.mikepenz.aboutlibraries.util.toStringArray
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.withIdentifier
import com.mikepenz.materialdrawer.model.interfaces.withName
import com.mikepenz.materialdrawer.model.interfaces.withSelectable

/**
 * Created by mikepenz on 04.06.14.
 */
class FragmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFragmentBinding

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

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
        binding = ActivityFragmentBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        //Remove line to test RTL support
        //window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL

        // Handle Toolbar
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        actionBarDrawerToggle = ActionBarDrawerToggle(this, binding.root, toolbar, R.string.material_drawer_open, R.string.material_drawer_close)

        binding.slider.apply {
            itemAdapter.add(
                    PrimaryDrawerItem().withName("Home"),
                    PrimaryDrawerItem().withName(R.string.action_manifestactivity).withIdentifier(R.id.action_manifestactivity.toLong()).withSelectable(false),
                    PrimaryDrawerItem().withName(R.string.action_minimalactivity).withIdentifier(R.id.action_minimalactivity.toLong()).withSelectable(false),
                    PrimaryDrawerItem().withName(R.string.action_extendactivity).withIdentifier(R.id.action_extendedactivity.toLong()).withSelectable(false),
                    PrimaryDrawerItem().withName(R.string.action_customsortactivity).withIdentifier(R.id.action_customsortactivity.toLong()).withSelectable(false),
                    PrimaryDrawerItem().withName(R.string.action_opensource).withIdentifier(R.id.action_opensource.toLong()).withSelectable(false)
            )
            onDrawerItemClickListener = { _, drawerItem, _ ->
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                when (drawerItem.identifier) {
                    R.id.action_opensource.toLong() -> {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mikepenz/AboutLibraries"))
                        startActivity(browserIntent)
                    }
                    R.id.action_extendedactivity.toLong() -> {
                        val intent = Intent(applicationContext, ExtendActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.action_customsortactivity.toLong() -> {
                        val intent = Intent(applicationContext, CustomSortActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.action_minimalactivity.toLong() -> {
                        // create and launch an activity in minimal design without any additional modifications
                        LibsBuilder()
                                .withFields(R.string::class.java.fields)
                                .withAboutMinimalDesign(true)
                                .withEdgeToEdge(true)
                                .withActivityTitle("Open Source")
                                .withAboutIconShown(false)
                                .withSearchEnabled(true)
                                .start(this@FragmentActivity)
                    }
                    R.id.action_manifestactivity.toLong() -> {
                        // create and launch an activity in full design, with various configurations and adjustments
                        LibsBuilder()
                                .withFields(R.string::class.java.fields)
                                .withLibraries("crouton", "actionbarsherlock", "showcaseview", "glide")
                                .withAutoDetect(false)
                                .withLicenseShown(true)
                                .withVersionShown(true)
                                .withActivityTitle("Open Source")
                                .withEdgeToEdge(true)
                                .withListener(libsListener)
                                .withLibTaskCallback(libTaskCallback)
                                .withUiListener(libsUIListener)
                                .withSearchEnabled(true)
                                .start(this@FragmentActivity)
                    }
                }
                false
            }
            selectedItemPosition = 0
        }

        /*
        //NOTE: This is how you can modify a specific library definition during runtime
        HashMap<String, HashMap<String, String>> libsModification = new HashMap<String, HashMap<String, String>>();
        HashMap<String, String> modifyAboutLibraries = new HashMap<String, String>();
        modifyAboutLibraries.put("name", "_AboutLibraries");
        libsModification.put("aboutlibraries", modifyAboutLibraries);
        .withLibraryModification(libsModification);
        */

        val fragment = LibsBuilder()
                .withFields(R.string::class.java.fields)
                .withVersionShown(false)
                .withLicenseShown(true)
                // find ids via './gradlew findLibraries'
                .withLibraryModification("androidx_activity__activity", Libs.LibraryFields.LIBRARY_NAME, "Activity Support")
                .withLibraryEnchantment("com_mikepenz__fastadapter", "fastadapter")
                .supportFragment()

        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()

        // Showcase to use the library meta information without the UI module
        Libs(this, R.string::class.java.fields.toStringArray())
                .prepareLibraries()
                .forEach {
                    Log.d("AboutLibraries", it.libraryName)
                }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        actionBarDrawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        actionBarDrawerToggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return actionBarDrawerToggle.onOptionsItemSelected(item)
    }
}
