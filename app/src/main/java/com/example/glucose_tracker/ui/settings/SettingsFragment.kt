package com.example.glucose_tracker.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.glucose_tracker.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}