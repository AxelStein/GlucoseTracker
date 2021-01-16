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
import com.axel_stein.glucose_tracker.MainNavDirections.Companion.actionAddGlucose
import com.axel_stein.glucose_tracker.MainNavDirections.Companion.actionAddInsulinLog
import com.axel_stein.glucose_tracker.MainNavDirections.Companion.actionAddMedicationLog
import com.axel_stein.glucose_tracker.MainNavDirections.Companion.actionAddNote
import com.axel_stein.glucose_tracker.MainNavDirections.Companion.actionOpenSettings
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.databinding.ActivityMainBinding
import com.axel_stein.glucose_tracker.utils.ui.AnimationHelper
import com.axel_stein.glucose_tracker.utils.ui.setShown
import javax.inject.Inject


class MainActivity : AppCompatActivity() {
    private val extraShowFab = "com.axel_stein.glucose_tracker.ui.SHOW_FAB"
    private val extraShowFabMenu = "com.axel_stein.glucose_tracker.ui.SHOW_FAB_MENU"
    private lateinit var binding: ActivityMainBinding
    private val animationHelper = AnimationHelper()
    private lateinit var navController: NavController
    private val destinationListener = NavController.OnDestinationChangedListener { _, destination, _ ->
        when(destination.id) {
            R.id.menu_home_fragment -> binding.fab.show()
            else -> binding.fab.hide()
        }
    }

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
                setOf(R.id.menu_home_fragment, R.id.menu_statistics_fragment, R.id.menu_archive_fragment, R.id.menu_plus_fragment)
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
                    R.id.btn_add_glucose -> actionAddGlucose()
                    R.id.btn_add_note -> actionAddNote()
                    R.id.btn_add_insulin -> actionAddInsulinLog()
                    R.id.btn_add_medication -> actionAddMedicationLog()
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

    override fun onStart() {
        super.onStart()
        navController.addOnDestinationChangedListener(destinationListener)
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(destinationListener)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> navController.navigate(actionOpenSettings())
        }
        return super.onOptionsItemSelected(item)
    }
}