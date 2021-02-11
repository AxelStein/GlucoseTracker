package com.axel_stein.glucose_tracker.utils.ui

import android.content.Context
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP

fun dpToPx(context: Context, dp: Float): Float {
    return TypedValue.applyDimension(COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
}

fun Float.intoPx(context: Context): Float {
    return TypedValue.applyDimension(COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)
}