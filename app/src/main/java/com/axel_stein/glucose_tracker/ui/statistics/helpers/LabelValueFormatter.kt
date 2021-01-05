package com.axel_stein.glucose_tracker.ui.statistics.helpers

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class LabelValueFormatter(private val months: Array<String>) : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return months.getOrNull(value.toInt()) ?: ""
    }
}