package com.axel_stein.glucose_tracker.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.axel_stein.glucose_tracker.databinding.ActivitySettingsBinding
import com.axel_stein.glucose_tracker.ui.ProgressListener
import com.axel_stein.glucose_tracker.utils.setViewVisible

class SettingsActivity : AppCompatActivity(), ProgressListener {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    override fun showProgress(show: Boolean) {
        setViewVisible(show, binding.progressBar)
    }
}