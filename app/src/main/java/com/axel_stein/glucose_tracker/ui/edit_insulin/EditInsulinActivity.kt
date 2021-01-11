package com.axel_stein.glucose_tracker.ui.edit_insulin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.axel_stein.glucose_tracker.databinding.ActivityEditInsulinBinding

class EditInsulinActivity : AppCompatActivity() {
    private val args: EditInsulinActivityArgs by navArgs()
    private lateinit var binding: ActivityEditInsulinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditInsulinBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}