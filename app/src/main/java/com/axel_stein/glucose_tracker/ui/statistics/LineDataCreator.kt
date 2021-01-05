package com.axel_stein.glucose_tracker.ui.statistics

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class LineDataCreator(
    private val entries: ArrayList<Entry>,
    private val lineColor: Int,
    private val fillColor: Int = lineColor
    ) {
    fun create(label: String? = null): LineData {
        val line = LineDataSet(entries, label)
        line.color = lineColor
        line.setCircleColor(lineColor)
        line.lineWidth = 1.5f
        line.circleRadius = 3f
        line.fillColor = fillColor
        line.setDrawFilled(true)
        line.setDrawCircleHole(false)
        line.setDrawHighlightIndicators(false)
        line.setDrawValues(false)
        return LineData(line)
    }
}