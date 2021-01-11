package com.axel_stein.glucose_tracker.ui.edit_insulin_log

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.axel_stein.glucose_tracker.databinding.ActivityEditInsulinLogBinding

class EditInsulinLogActivity : AppCompatActivity() {
    private val args: EditInsulinLogActivityArgs by navArgs()
    private lateinit var binding: ActivityEditInsulinLogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditInsulinLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}