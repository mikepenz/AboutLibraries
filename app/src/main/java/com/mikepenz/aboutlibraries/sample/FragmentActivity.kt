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
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.LibsConfiguration
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.sample.databinding.ActivityFragmentBinding
import com.mikepenz.aboutlibraries.util.SpecialButton
import com.mikepenz.aboutlibraries.util.withContext
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.nameRes
import com.mikepenz.materialdrawer.model.interfaces.nameText
import com.mikepenz.materialdrawer.util.addStickyDrawerItems

/**
 * Created by mikepenz on 04.06.14.
 */
class FragmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFragmentBinding

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    private var libsUIListener: LibsConfiguration.LibsUIListener = object : LibsConfiguration.LibsUIListener {
        override fun preOnCreateView(view: View): View {
            return view
        }

        override fun postOnCreateView(view: View): View {
            return view
        }
    }

    private var libsListener: LibsConfiguration.LibsListener = object : LibsConfiguration.LibsListener {
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

        override fun onExtraClicked(v: View, specialButton: SpecialButton): Boolean {
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

        // Remove line to test RTL support
        // window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL

        // Handle Toolbar
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        actionBarDrawerToggle = ActionBarDrawerToggle(this, binding.root, toolbar, R.string.material_drawer_open, R.string.material_drawer_close)

        binding.slider.apply {
            itemAdapter.add(
                PrimaryDrawerItem().apply { nameText = "Home" },
                PrimaryDrawerItem().apply {
                    nameRes = R.string.action_composeactivity; isSelectable = false
                    onDrawerItemClickListener = { _, _, _ ->
                        startActivity(Intent(applicationContext, ComposeActivity::class.java))
                        false
                    }
                },
                PrimaryDrawerItem().apply {
                    nameRes = R.string.action_manifestactivity; identifier = R.id.action_manifestactivity.toLong(); isSelectable = false
                },
                PrimaryDrawerItem().apply {
                    nameRes = R.string.action_minimalactivity; identifier = R.id.action_minimalactivity.toLong(); isSelectable = false
                },
                PrimaryDrawerItem().apply {
                    nameRes = R.string.action_extendactivity; identifier = R.id.action_extendedactivity.toLong(); isSelectable = false
                },
                PrimaryDrawerItem().apply {
                    nameRes = R.string.action_customsortactivity; identifier = R.id.action_customsortactivity.toLong(); isSelectable = false
                }
            )
            addStickyDrawerItems(
                PrimaryDrawerItem().apply {
                    nameRes = R.string.action_opensource; identifier = R.id.action_opensource.toLong(); isSelectable = false
                }
            )
            onDrawerItemClickListener = { _, drawerItem, _ ->
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                when (drawerItem.identifier.toInt()) {
                    R.id.action_opensource -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mikepenz/AboutLibraries")))
                    R.id.action_extendedactivity -> startActivity(Intent(applicationContext, ExtendActivity::class.java))
                    R.id.action_customsortactivity -> startActivity(Intent(applicationContext, CustomSortActivity::class.java))
                    R.id.action_minimalactivity -> {
                        // create and launch an activity in minimal design without any additional modifications
                        LibsBuilder()
                            .withAboutMinimalDesign(true)
                            .withEdgeToEdge(true)
                            .withActivityTitle("Open Source")
                            .withAboutIconShown(false)
                            .withSearchEnabled(true)
                            .start(this@FragmentActivity)
                    }
                    R.id.action_manifestactivity -> {
                        // create and launch an activity in full design, with various configurations and adjustments
                        LibsBuilder()
                            .withLicenseShown(true)
                            .withVersionShown(true)
                            .withActivityTitle("Open Source")
                            .withEdgeToEdge(true)
                            .withListener(libsListener)
                            .withUiListener(libsUIListener)
                            .withSearchEnabled(true)
                            .start(this@FragmentActivity)
                    }
                }
                false
            }
            selectedItemPosition = 0
        }

        val fragment = LibsBuilder()
            .withVersionShown(true)
            .withLicenseShown(true)
            .withLicenseDialog(true)
            .supportFragment()

        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()

        // Showcase to use the library meta information without the UI module
        Libs.Builder().withContext(this).build().libraries
            .forEach {
                Log.d("AboutLibraries", it.name)
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
