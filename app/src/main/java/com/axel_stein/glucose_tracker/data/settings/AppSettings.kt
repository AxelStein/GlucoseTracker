package com.axel_stein.glucose_tracker.data.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

class AppSettings(ctx: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)

    init {
        enableNightMode(prefs.getBoolean("night_mode", false))
    }

    fun enableNightMode(enable: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enable) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

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