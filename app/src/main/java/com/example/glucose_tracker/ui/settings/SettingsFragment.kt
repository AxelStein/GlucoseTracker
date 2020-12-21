package com.example.glucose_tracker.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.glucose_tracker.R


class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val glucoseUnits = preferenceManager.findPreference<ListPreference>("glucose_units")
        glucoseUnits?.setOnPreferenceChangeListener { preference, newValue ->
            Log.d("TAG", "onPreferenceChange $newValue")
            true
        }
    }
}