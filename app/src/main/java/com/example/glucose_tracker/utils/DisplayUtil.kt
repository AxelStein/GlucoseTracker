package com.example.glucose_tracker.utils

import android.content.Context
import android.util.DisplayMetrics
import kotlin.math.roundToInt

fun dpToPx(context: Context?, dp: Int): Int {
    if (context == null || dp < 0) {
        return 0
    }
    val displayMetrics = context.resources.displayMetrics
    return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun pxToDp(context: Context?, px: Int): Int {
    if (context == null || px < 0) {
        return 0
    }
    val displayMetrics = context.resources.displayMetrics
    return (px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}
