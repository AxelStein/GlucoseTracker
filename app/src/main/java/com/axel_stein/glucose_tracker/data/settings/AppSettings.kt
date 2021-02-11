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
        var feet = getOrZero("height_feet")
        var inches = getOrZero("height_inches")
        val height = getHeight()

        if (feet == "0" && inches == "0" && height != "0") {
            try {
                val pair = heightIntoImperial(height.toInt())
                feet = pair.first.toString()
                inches = pair.second.toString()
            } catch (e: Exception) {
                feet = "0"
                inches = "0"
            }
        }
        return feet to inches
    }

    fun setHeightImperial(feet: String, inches: String) {
        prefs.edit()
            .putString("height_feet", feet)
            .putString("height_inches", inches)
            .apply()

        val height = try {
            heightIntoMetric(feet.toInt(), inches.toFloat()).toString()
        } catch (e: Exception) {
            "0"
        }
        prefs.edit().putString("height", height).apply()
    }

    fun getHeight() = getOrZero("height")

    fun setHeight(height: String) {
        val h = try {
            height.toInt()
        } catch (e: Exception) {
            0
        }
        val (feet, inches) = heightIntoImperial(h)
        prefs.edit()
            .putString("height", h.toString())
            .putString("height_feet", feet.toString())
            .putString("height_inches", inches.round().toString())
            .apply()
    }

    private fun getOrZero(key: String): String {
        val s = prefs.getString(key, "0")
        return if (s.isNullOrEmpty()) {
            "0"
        } else {
            s
        }
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