package com.axel_stein.glucose_tracker.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.ui.edit_glucose.EditGlucoseActivity
import com.axel_stein.glucose_tracker.ui.edit_note.EdiNoteActivity
import com.axel_stein.glucose_tracker.utils.hide
import com.axel_stein.glucose_tracker.utils.setShown
import com.axel_stein.glucose_tracker.utils.show
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var dim: View
    private lateinit var fab: FloatingActionButton
    private lateinit var fabMenu: View
    private val extraShowFab = "com.axel_stein.glucose_tracker.ui.SHOW_FAB"
    private val extraShowFabMenu = "com.axel_stein.glucose_tracker.ui.SHOW_FAB_MENU"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        fab = findViewById(R.id.fab)
        fabMenu = findViewById(R.id.fab_menu)
        dim = findViewById(R.id.dim)

        if (savedInstanceState != null) {
            fab.setShown(savedInstanceState.getBoolean(extraShowFab, true))

            val showFabMenu = savedInstanceState.getBoolean(extraShowFabMenu, true)
            fabMenu.setShown(showFabMenu)
            dim.setShown(showFabMenu)
        }

        dim.setOnClickListener {
            it.hide()
            fabMenu.hide()
            fab.show()
        }

        fab.setOnClickListener {
            fab.hide()
            fabMenu.show()
            dim.show()
        }

        findViewById<View>(R.id.btn_add_glucose).setOnClickListener {
            EditGlucoseActivity.launch(this)
            dim.performClick()
        }

        findViewById<View>(R.id.btn_add_note).setOnClickListener {
            EdiNoteActivity.launch(this)
            dim.performClick()
        }

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        bottomNavView.setupWithNavController(navController)
        bottomNavView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.menu_home -> fab.show()
                else -> fab.hide()
            }
            NavigationUI.onNavDestinationSelected(item, navController)
        }
    }

    override fun onBackPressed() {
        if (dim.isShown) {
            dim.performClick()
        } else {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(extraShowFab, fab.isShown)
        outState.putBoolean(extraShowFabMenu, fabMenu.isShown)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}