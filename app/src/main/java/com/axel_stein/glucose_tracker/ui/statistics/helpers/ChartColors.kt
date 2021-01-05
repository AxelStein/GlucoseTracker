package com.axel_stein.glucose_tracker.ui.statistics.helpers

import android.content.Context
import android.graphics.Color
import com.axel_stein.glucose_tracker.R
import com.google.android.material.color.MaterialColors

class ChartColors(ctx: Context) {
    val beforeMealLineColor = MaterialColors.getColor(ctx, R.attr.beforeMealLineColor, Color.BLACK)
    val beforeMealFillColor = MaterialColors.getColor(ctx, R.attr.beforeMealFillColor, Color.BLACK)
    val afterMealLineColor = MaterialColors.getColor(ctx, R.attr.afterMealLineColor, Color.BLACK)
    val afterMealFillColor = MaterialColors.getColor(ctx, R.attr.afterMealFillColor, Color.BLACK)
    val a1cLineColor = MaterialColors.getColor(ctx, R.attr.a1cLineColor, Color.BLACK)
    val a1cFillColor = MaterialColors.getColor(ctx, R.attr.a1cFillColor, Color.BLACK)
}