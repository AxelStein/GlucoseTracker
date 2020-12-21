package com.example.glucose_tracker.data.settings

import androidx.preference.PreferenceManager
import com.example.glucose_tracker.ui.App

class AppSettings(val app: App) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(app)

    fun useMmolAsGlucoseUnits(): Boolean {
        return when(prefs.getString("glucose_units", "mmol_l")) {
            "mmol_l" -> true
            else -> false
        }
    }
}