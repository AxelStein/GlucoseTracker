package com.axel_stein.glucose_tracker.data.settings

import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

class AppSettings(ctx: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)

    init {
        // Night mode pref was removed in version 1.0.7
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

    fun setHeight(height: String) {
        prefs.edit().putString("height", height).apply()
    }

    fun observeGlucoseUnits(): Flowable<Boolean> {
        return Flowable.create({ emitter ->
            var oldVal = useMmolAsGlucoseUnits()
            val listener = OnSharedPreferenceChangeListener { _, key ->
                if (key == "glucose_units") {
                    val newVal = useMmolAsGlucoseUnits()
                    if (oldVal != newVal) {
                        oldVal = newVal
                        emitter.onNext(newVal)
                    }
                }
            }
            prefs.registerOnSharedPreferenceChangeListener(listener)
            emitter.setCancellable {
                prefs.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }, BackpressureStrategy.LATEST)
    }
}