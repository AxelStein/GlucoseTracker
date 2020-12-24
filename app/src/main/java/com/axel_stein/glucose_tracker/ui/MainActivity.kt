package com.axel_stein.glucose_tracker.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewAnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.ui.edit_a1c.EditA1cActivity
import com.axel_stein.glucose_tracker.ui.edit_glucose.EditGlucoseActivity
import com.axel_stein.glucose_tracker.ui.edit_note.EditNoteActivity
import com.axel_stein.glucose_tracker.utils.setShown
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {
    private val extraShowFab = "com.axel_stein.glucose_tracker.ui.SHOW_FAB"
    private val extraShowFabMenu = "com.axel_stein.glucose_tracker.ui.SHOW_FAB_MENU"

    private lateinit var dim: View
    private lateinit var fab: FloatingActionButton
    private lateinit var fabMenu: View

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
            fabMenu.visibility = if (showFabMenu) VISIBLE else INVISIBLE
            dim.visibility = if (showFabMenu) VISIBLE else INVISIBLE
        }

        dim.setOnClickListener {
            unRevealDim()
            unRevealFabMenu()
            fab.show()
        }

        fab.setOnClickListener {
            fab.hide()
            revealFabMenu()
            revealDim()
        }

        findViewById<View>(R.id.btn_add_glucose).setOnClickListener {
            EditGlucoseActivity.launch(this)
            dim.performClick()
        }

        findViewById<View>(R.id.btn_add_note).setOnClickListener {
            EditNoteActivity.launch(this)
            dim.performClick()
        }
        findViewById<View>(R.id.btn_add_a1c).setOnClickListener {
            EditA1cActivity.launch(this)
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

    private fun revealFabMenu() {
        revealView(fabMenu, fabMenu.width, fabMenu.height)
    }

    private fun unRevealFabMenu() {
        unRevealView(fabMenu, fabMenu.width, fabMenu.height)
    }

    private fun revealDim() {
        revealView(dim, fab.centerX(), fab.centerY())
    }

    private fun unRevealDim() {
        unRevealView(dim, fab.centerX(), fab.centerY())
    }

    private fun revealView(view: View, x: Int, y: Int) {
        val endRadius = (view.width + view.height).toFloat()
        val animator = ViewAnimationUtils.createCircularReveal(view, x, y, 0f, endRadius)
                .setDuration(400)
        view.visibility = VISIBLE
        animator.start()
    }

    private fun unRevealView(view: View, x: Int, y: Int) {
        val endRadius = (view.width + view.height).toFloat()
        val animator = ViewAnimationUtils.createCircularReveal(view, x, y, endRadius, 0f)
                .setDuration(400)
        animator.addListener(onEnd = {
            view.visibility = INVISIBLE
        })
        animator.start()
    }

    private fun View.centerX(): Int {
        return (this.x + this.width / 2).toInt()
    }

    private fun View.centerY(): Int {
        return (this.y + this.height / 2).toInt()
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