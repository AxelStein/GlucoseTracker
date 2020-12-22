package com.axel_stein.glucose_tracker.data.settings

import androidx.preference.PreferenceManager
import com.axel_stein.glucose_tracker.ui.App

class AppSettings(val app: App) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(app)

    fun useMmolAsGlucoseUnits(): Boolean {
        return when(getGlucoseUnits()) {
            "mmol_l" -> true
            else -> false
        }
    }

    fun getGlucoseUnits(): String {
        return prefs.getString("glucose_units", "mmol_l") ?: "mmol_l"
    }

    fun setGlucoseUnits(units: String) {
        prefs.edit().putString("glucose_units", units).apply()
    }
}