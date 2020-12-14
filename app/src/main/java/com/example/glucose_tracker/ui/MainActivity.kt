package com.example.glucose_tracker.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.glucose_tracker.R
import com.example.glucose_tracker.ui.edit_glucose.EditGlucoseActivity
import com.example.glucose_tracker.ui.log_list.LogListFragment
import com.example.glucose_tracker.utils.hide
import com.example.glucose_tracker.utils.show
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        supportFragmentManager.beginTransaction()
            .replace(R.id.content, LogListFragment(), "LogList")
            .commit()

        val fab = findViewById<ExtendedFloatingActionButton>(R.id.fab)
        val fabMenu = findViewById<View>(R.id.fab_menu)
        val dim = findViewById<View>(R.id.dim)

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