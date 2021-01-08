package com.axel_stein.glucose_tracker.ui

import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewAnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.databinding.ActivityMainBinding
import com.axel_stein.glucose_tracker.ui.edit_a1c.EditA1cActivityDirections.Companion.launchEditA1c
import com.axel_stein.glucose_tracker.ui.edit_glucose.EditGlucoseActivity
import com.axel_stein.glucose_tracker.ui.edit_note.EditNoteActivityDirections.Companion.launchEditNote
import com.axel_stein.glucose_tracker.utils.setShown
import com.axel_stein.glucose_tracker.utils.setViewVisible


class MainActivity : AppCompatActivity(), ProgressListener {
    private val extraShowFab = "com.axel_stein.glucose_tracker.ui.SHOW_FAB"
    private val extraShowFabMenu = "com.axel_stein.glucose_tracker.ui.SHOW_FAB_MENU"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if (savedInstanceState != null) {
            binding.fab.setShown(savedInstanceState.getBoolean(extraShowFab, true))

            val showFabMenu = savedInstanceState.getBoolean(extraShowFabMenu, true)
            binding.fabMenu.visibility = if (showFabMenu) VISIBLE else INVISIBLE
            binding.dim.visibility = if (showFabMenu) VISIBLE else INVISIBLE
        }

        binding.dim.setOnClickListener {
            unRevealDim()
            unRevealFabMenu()
            binding.fab.show()
        }

        binding.fab.setOnClickListener {
            binding.fab.hide()
            revealFabMenu()
            revealDim()
        }

        binding.btnAddGlucose.setOnClickListener {
            EditGlucoseActivity.launch(this)
            binding.dim.performClick()
        }

        binding.btnAddNote.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(launchEditNote())
            binding.dim.performClick()
        }
        binding.btnAddA1c.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(launchEditA1c())
            binding.dim.performClick()
        }

        binding.bottomNavView.setupWithNavController(findNavController(R.id.nav_host_fragment))
        binding.bottomNavView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.menu_home -> binding.fab.show()
                else -> binding.fab.hide()
            }
            NavigationUI.onNavDestinationSelected(item, findNavController(R.id.nav_host_fragment))
        }
    }

    private fun revealFabMenu() {
        revealView(binding.fabMenu, binding.fabMenu.width, binding.fabMenu.height)
    }

    private fun unRevealFabMenu() {
        unRevealView(binding.fabMenu, binding.fabMenu.width, binding.fabMenu.height)
    }

    private fun revealDim() {
        revealView(binding.dim, binding.fab.centerX(), binding.fab.centerY())
    }

    private fun unRevealDim() {
        unRevealView(binding.dim, binding.fab.centerX(), binding.fab.centerY())
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
}