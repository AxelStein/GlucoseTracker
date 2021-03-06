package com.axel_stein.glucose_tracker.data.settings

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color.BLACK
import androidx.appcompat.app.AppCompatActivity
import com.axel_stein.glucose_tracker.R
import com.google.android.material.color.MaterialColors.getColor
import java.io.File

class AppResources(private val ctx: Context) {
    val mmolSuffix = ctx.getString(R.string.glucose_unit_mmol_l)
    val mgSuffix = ctx.getString(R.string.glucose_unit_mg_dl)
    val kgSuffix = ctx.getString(R.string.weight_unit_kg)
    val lbSuffix = ctx.getString(R.string.weight_unit_lb)
    val feetSuffix = ctx.getString(R.string.feet_suffix)
    val inchesSuffix = ctx.getString(R.string.inches_suffix)
    val measuredArray: Array<String> = ctx.resources.getStringArray(R.array.measured)
    val monthsArray: Array<String> = ctx.resources.getStringArray(R.array.months)
    val monthsAbbrArray: Array<String> = ctx.resources.getStringArray(R.array.months_a)
    val dosageFormsPlurals: TypedArray = ctx.resources.obtainTypedArray(R.array.dosage_form_plurals)

    private var beforeMealLineColor = 0
    private var beforeMealFillColor = 0
    private var afterMealLineColor = 0
    private var afterMealFillColor = 0
    private var a1cLineColor = 0
    private var a1cFillColor = 0
    private var weightLineColor = 0
    private var weightFillColor = 0

    fun initColorResources(context: AppCompatActivity) {
        beforeMealLineColor = getColor(context, R.attr.beforeMealLineColor, BLACK)
        beforeMealFillColor = getColor(context, R.attr.beforeMealFillColor, BLACK)
        afterMealLineColor = getColor(context, R.attr.afterMealLineColor, BLACK)
        afterMealFillColor = getColor(context, R.attr.afterMealFillColor, BLACK)
        a1cLineColor = getColor(context, R.attr.a1cLineColor, BLACK)
        a1cFillColor = getColor(context, R.attr.a1cFillColor, BLACK)
        weightLineColor = getColor(context, R.attr.weightLineColor, BLACK)
        weightFillColor = getColor(context, R.attr.weightFillColor, BLACK)
    }

    fun appDir(): File = ctx.filesDir

    fun beforeMealLineColor() = beforeMealLineColor
    fun beforeMealFillColor() = beforeMealFillColor
    fun afterMealLineColor() = afterMealLineColor
    fun afterMealFillColor() = afterMealFillColor
    fun a1cLineColor() = a1cLineColor
    fun a1cFillColor() = a1cFillColor
    fun weightLineColor() = weightLineColor
    fun weightFillColor() = weightFillColor
}