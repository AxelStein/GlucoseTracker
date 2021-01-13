package com.axel_stein.glucose_tracker.ui.plus

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.ui.plus.PlusFragmentDirections.Companion.actionMenuPlusToA1cList
import com.axel_stein.glucose_tracker.ui.plus.PlusFragmentDirections.Companion.actionMenuPlusToInsulinList
import com.axel_stein.glucose_tracker.ui.plus.PlusFragmentDirections.Companion.actionMenuPlusToMedicationList

class PlusFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.plus_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val insulinPref = preferenceManager.findPreference<Preference>("insulin_list")
        insulinPref?.setOnPreferenceClickListener {
            findNavController().navigate(actionMenuPlusToInsulinList())
            true
        }

        val medicationsPref = preferenceManager.findPreference<Preference>("medications")
        medicationsPref?.setOnPreferenceClickListener {
            findNavController().navigate(actionMenuPlusToMedicationList())
            true
        }

        val a1cListPref = preferenceManager.findPreference<Preference>("a1c_list")
        a1cListPref?.setOnPreferenceClickListener {
            findNavController().navigate(actionMenuPlusToA1cList())
            true
        }
    }
}