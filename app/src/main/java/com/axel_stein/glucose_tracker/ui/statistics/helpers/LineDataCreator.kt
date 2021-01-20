package com.axel_stein.glucose_tracker.ui.statistics.helpers

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class LineDataCreator {
    private val list = ArrayList<LineDataSet>()

    fun from(entries: List<Entry>, lineColor: Int, fillColor: Int = lineColor): LineDataCreator {
        list.add(createLine(entries, lineColor, fillColor))
        return this
    }

    private fun createLine(entries: List<Entry>, lineColor: Int, fillColor: Int = lineColor): LineDataSet {
        return LineDataSet(entries, null)
            .apply {
                color = lineColor
                setCircleColor(lineColor)
                lineWidth = 1.5f
                circleRadius = 3f
                if (fillColor != -1) {
                    this.fillColor = fillColor
                    setDrawFilled(true)
                } else {
                    setDrawFilled(false)
                }
                setDrawCircleHole(false)
                setDrawHighlightIndicators(false)
                setDrawValues(false)
            }
    }

    fun create() = LineData(list.toList())
}