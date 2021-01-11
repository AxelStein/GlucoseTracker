package com.axel_stein.glucose_tracker.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.databinding.ActivityMainBinding
import com.axel_stein.glucose_tracker.ui.edit_a1c.EditA1cActivityDirections.Companion.launchEditInsulin
import com.axel_stein.glucose_tracker.ui.edit_glucose.EditGlucoseActivityDirections.Companion.launchEditGlucose
import com.axel_stein.glucose_tracker.ui.edit_note.EditNoteActivityDirections.Companion.launchEditNote
import com.axel_stein.glucose_tracker.ui.settings.SettingsActivityDirections.Companion.launchSettings
import com.axel_stein.glucose_tracker.utils.setShown
import com.axel_stein.glucose_tracker.utils.setViewVisible
import javax.inject.Inject


class MainActivity : AppCompatActivity(), ProgressListener {
    private val extraShowFab = "com.axel_stein.glucose_tracker.ui.SHOW_FAB"
    private val extraShowFabMenu = "com.axel_stein.glucose_tracker.ui.SHOW_FAB_MENU"
    private lateinit var binding: ActivityMainBinding
    private val animationHelper = AnimationHelper()
    private lateinit var navController: NavController

    @Inject
    lateinit var appSettings: AppSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            navController.navigateUp()
        }

        NavigationUI.setupActionBarWithNavController(this, navController,
            AppBarConfiguration(
                    setOf(R.id.menu_home, R.id.menu_stats, R.id.menu_archive, R.id.menu_plus)
            )
        )

        if (savedInstanceState != null) {
            binding.fab.setShown(savedInstanceState.getBoolean(extraShowFab, true))
        }

        binding.fabMenu.post {
            animationHelper.initFabMenu(binding.fabMenu)
        }
        binding.fabMenu.forEach { child ->
            child.setOnClickListener {
                val dest = when (it.id) {
                    R.id.btn_add_glucose -> launchEditGlucose()
                    R.id.btn_add_note -> launchEditNote()
                    R.id.btn_add_insulin -> launchEditInsulin()
                    else -> TODO("Not implemented")
                }
                navController.navigate(dest)
                hideFabMenu()
            }
        }

        binding.dim.setOnClickListener {
            hideFabMenu()
        }

        binding.fab.setOnClickListener {
            if (binding.dim.isShown) {
                hideFabMenu()
            } else {
                showFabMenu()
            }
        }

        binding.bottomNavView.setupWithNavController(navController)
        binding.bottomNavView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.menu_home -> binding.fab.show()
                else -> binding.fab.hide()
            }
            NavigationUI.onNavDestinationSelected(item, navController)
        }
    }

    private fun showFabMenu() {
        animationHelper.rotateFab(binding.fab, true)
        animationHelper.showDim(binding.dim)
        animationHelper.showFabMenu(binding.fabMenu)
    }

    private fun hideFabMenu() {
        animationHelper.rotateFab(binding.fab, false)
        animationHelper.hideDim(binding.dim)
        animationHelper.hideFabMenu(binding.fabMenu)
    }

    override fun onBackPressed() {
        if (binding.dim.isShown) {
            binding.dim.performClick()
        } else {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(extraShowFab, binding.fab.isShown)
        outState.putBoolean(extraShowFabMenu, binding.fabMenu.isShown)
    }

    override fun showProgress(show: Boolean) {
        setViewVisible(show, binding.progressBar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> navController.navigate(launchSettings())
        }
        return super.onOptionsItemSelected(item)
    }
}