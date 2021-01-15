package com.axel_stein.glucose_tracker.data.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

class AppSettings(ctx: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)

    init {
        // Workaround for version 1.0.6
        if (prefs.contains("night_mode")) {
            prefs.edit().remove("night_mode").apply()
        }
        applyTheme(prefs.getString("theme", "system") ?: "system")
    }

    fun applyTheme(theme: String) {
        AppCompatDelegate.setDefaultNightMode(when (theme) {
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        })
    }

    fun useMmolAsGlucoseUnits() = when(getGlucoseUnits()) {
        "mmol_l" -> true
        else -> false
    }

    fun getGlucoseUnits() = prefs.getString("glucose_units", "mmol_l") ?: "mmol_l"

    fun setGlucoseUnits(units: String) {
        prefs.edit().putString("glucose_units", units).apply()
    }

    fun getHeight() = prefs.getString("height", "0") ?: "0"
}