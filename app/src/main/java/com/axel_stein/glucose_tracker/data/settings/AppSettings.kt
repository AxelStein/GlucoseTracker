package com.axel_stein.glucose_tracker.data.settings

import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.axel_stein.glucose_tracker.utils.heightIntoImperial
import com.axel_stein.glucose_tracker.utils.heightIntoMetric
import com.axel_stein.glucose_tracker.utils.round
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

    fun getHeightImperial(): Pair<String, String> {
        var feet = prefs.getString("height_feet", "0") ?: "0"
        var inches = prefs.getString("height_inches", "0") ?: "0"
        if (feet == "0" && inches == "0" && getHeight() != "0") {
            val pair = heightIntoImperial(getHeight().toInt())
            feet = pair.first.toString()
            inches = pair.second.toString()
        }
        return feet to inches
    }

    fun setHeightImperial(feet: String, inches: String) {
        prefs.edit()
            .putString("height_feet", feet)
            .putString("height_inches", inches)
            .putString("height", heightIntoMetric(feet.toInt(), inches.toFloat()).toString())
            .apply()
    }

    fun getHeight() = prefs.getString("height", "0") ?: "0"

    fun setHeight(height: String) {
        val (feet, inches) = heightIntoImperial(height.toInt())
        prefs.edit()
            .putString("height", height)
            .putString("height_feet", feet.toString())
            .putString("height_inches", inches.round().toString())
            .apply()
    }

    fun useMetricSystem() = prefs.getString("measurement_system", "metric") == "metric"

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

    fun observeMeasurementSystem(): Flowable<Boolean> {
        return Flowable.create({ emitter ->
            var oldVal = useMetricSystem()
            val listener = OnSharedPreferenceChangeListener { _, currentKey ->
                if (currentKey == "measurement_system") {
                    val newVal = useMetricSystem()
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