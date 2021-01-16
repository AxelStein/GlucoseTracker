package com.axel_stein.glucose_tracker.data.settings

import android.content.Context
import android.content.res.TypedArray
import com.axel_stein.glucose_tracker.R
import java.io.File

class AppResources(private val ctx: Context, private val settings: AppSettings) {
    val mmolSuffix = ctx.getString(R.string.glucose_unit_mmol_l)
    val mgSuffix = ctx.getString(R.string.glucose_unit_mg_dl)
    val kgSuffix = ctx.getString(R.string.weight_unit_kg)
    val measuredArray: Array<String> = ctx.resources.getStringArray(R.array.measured)
    val monthsArray: Array<String> = ctx.resources.getStringArray(R.array.months)
    val monthsAbbrArray: Array<String> = ctx.resources.getStringArray(R.array.months_a)
    val dosageFormsPlurals: TypedArray = ctx.resources.obtainTypedArray(R.array.dosage_form_plurals)

    fun appDir(): File = ctx.filesDir

    fun currentSuffix() = if (settings.useMmolAsGlucoseUnits()) mmolSuffix else mgSuffix
}