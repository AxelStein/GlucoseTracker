package com.axel_stein.glucose_tracker.utils

import kotlin.math.floor
import kotlin.math.roundToInt

fun Int.intoMmol() = this.div(18f)

fun Float.intoMgDl() = this.times(18f).toInt()

fun Float.round() = this.times(10f).roundToInt().toFloat().div(10f)

fun Float.intoKg() = this.div(2.2f)

fun Float.intoLb() = this.times(2.2f)

fun Float.intoMeter() = this.div(100f)

fun Float.formatRoundIfInt(): String {
    return if (rem(1).equals(0f)) {
        toInt().toString()
    } else {
        toString()
    }
}

fun cmIntoInches(cm: Int) = cm * 0.39370079f

fun inchesIntoFeet(inches: Float) = inches * 0.08333333f

fun heightIntoImperial(height: Int): Pair<Int, Float> {
    val totalInches = cmIntoInches(height)
    val feet = floor(inchesIntoFeet(totalInches)).toInt()
    val inches = (totalInches - feet * 12f)
    return feet to inches
}

fun heightIntoMetric(feet: Int, inches: Float): Int {
    val totalInches = (feet * 12f) + inches
    return (totalInches * 2.54f).roundToInt()
}