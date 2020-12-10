package com.example.glucose_tracker.ui.edit_glucose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.glucose_tracker.R

class EditGlucoseActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_glucose)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}