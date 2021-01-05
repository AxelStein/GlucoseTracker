package com.axel_stein.glucose_tracker.data.settings

import android.content.Context
import com.axel_stein.glucose_tracker.R
import java.io.File

class AppResources(val ctx: Context, val settings: AppSettings) {
    val mmolSuffix = ctx.getString(R.string.mmol_l)
    val mgSuffix = ctx.getString(R.string.mg_dl)

    fun appDir(): File {
        return ctx.filesDir
    }

    fun currentSuffix() = if (settings.useMmolAsGlucoseUnits()) mmolSuffix else mgSuffix

    fun measuredArray(): Array<String> = ctx.resources.getStringArray(R.array.measured)

    fun monthsArray(): Array<String> = ctx.resources.getStringArray(R.array.months)

    fun monthsAbbrArray(): Array<String> = ctx.resources.getStringArray(R.array.months_a)

}