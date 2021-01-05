package com.axel_stein.glucose_tracker.ui.statistics

import android.graphics.Color
import com.axel_stein.glucose_tracker.data.model.GlucoseLog
import com.github.mikephil.charting.data.Entry

class GlucoseLineDataHelper(
    logs: List<GlucoseLog>,
    private val hypoThreshold: Float,
    private val hyperThreshold: Float,
    private var measureFilter: IntArray = intArrayOf(),
    private var lineColor: Int = Color.BLACK,
    private var fillColor: Int = lineColor
) {
    private var maxValue = 0f
    private var hypoCount = 0
    private var hyperCount = 0
    private val entries = ArrayList<Entry>()

    init {
        logs.filter { item -> item.measured in measureFilter }
        .forEachIndexed { i, log ->
            if (log.valueMmol > maxValue) {
                maxValue = log.valueMmol
            }
            if (log.valueMmol < hypoThreshold) {
                hypoCount++
            }
            if (log.valueMmol > hyperThreshold) {
                hyperCount++
            }
            entries.add(Entry(i.toFloat(), log.valueMmol))
        }
    }

    fun maxValue() = maxValue

    fun hypoCount() = hypoCount

    fun hyperCount() = hyperCount

    fun createLineData() = LineDataCreator(entries, lineColor, fillColor).create()
}