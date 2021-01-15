package com.axel_stein.glucose_tracker.data.settings

import android.content.Context
import com.axel_stein.glucose_tracker.R
import java.io.File

class AppResources(private val ctx: Context, private val settings: AppSettings) {
    val mmolSuffix = ctx.getString(R.string.glucose_unit_mmol_l)
    val mgSuffix = ctx.getString(R.string.glucose_unit_mg_dl)
    val unitsSuffix = ctx.getString(R.string.insulin_units_suffix)
    val kgSuffix = ctx.getString(R.string.weight_unit_kg)
    val measuredArray: Array<String> = ctx.resources.getStringArray(R.array.measured)
    val monthsArray: Array<String> = ctx.resources.getStringArray(R.array.months)
    val monthsAbbrArray: Array<String> = ctx.resources.getStringArray(R.array.months_a)
    val dosageFormsArray: Array<String> = ctx.resources.getStringArray(R.array.dosage_forms)

    fun appDir(): File = ctx.filesDir

    fun currentSuffix() = if (settings.useMmolAsGlucoseUnits()) mmolSuffix else mgSuffix
}