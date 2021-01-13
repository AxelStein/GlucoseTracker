package com.axel_stein.glucose_tracker.utils

import kotlin.math.roundToInt

fun Int.intoMmol(): Float = this.div(18f)

fun Float.intoMgDl(): Int = this.times(18f).toInt()

fun Float.round(): Float = this.times(10f).roundToInt().toFloat().div(10f)

fun Float.formatIfInt(): String {
    return if (rem(1).equals(0f)) {
        toInt().toString()
    } else {
        toString()
    }
}