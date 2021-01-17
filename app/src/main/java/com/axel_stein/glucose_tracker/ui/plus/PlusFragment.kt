package com.axel_stein.glucose_tracker.ui.plus

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.ui.plus.PlusFragmentDirections.Companion.actionOpenA1cList
import com.axel_stein.glucose_tracker.ui.plus.PlusFragmentDirections.Companion.actionOpenInsulinList
import com.axel_stein.glucose_tracker.ui.plus.PlusFragmentDirections.Companion.actionOpenMedicationList
import com.axel_stein.glucose_tracker.ui.plus.PlusFragmentDirections.Companion.actionOpenWeightList
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis

class PlusFragment : PreferenceFragmentCompat() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.plus_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val insulinPref = preferenceManager.findPreference<Preference>("insulin_list")
        insulinPref?.setOnPreferenceClickListener {
            overrideTransitions()
            findNavController().navigate(actionOpenInsulinList())
            true
        }

        val medicationsPref = preferenceManager.findPreference<Preference>("medications")
        medicationsPref?.setOnPreferenceClickListener {
            overrideTransitions()
            findNavController().navigate(actionOpenMedicationList())
            true
        }

        val a1cListPref = preferenceManager.findPreference<Preference>("a1c_list")
        a1cListPref?.setOnPreferenceClickListener {
            overrideTransitions()
            findNavController().navigate(actionOpenA1cList())
            true
        }

        val weightListPref = preferenceManager.findPreference<Preference>("weight_list")
        weightListPref?.setOnPreferenceClickListener {
            overrideTransitions()
            findNavController().navigate(actionOpenWeightList())
            true
        }
    }

    private fun overrideTransitions() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }
}