package com.axel_stein.glucose_tracker.ui.plus

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.axel_stein.glucose_tracker.R

class PlusFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.plus_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val insulinPref = preferenceManager.findPreference<Preference>("insulin")
        insulinPref?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_menu_plus_to_insulin_list)
            true
        }
    }
}