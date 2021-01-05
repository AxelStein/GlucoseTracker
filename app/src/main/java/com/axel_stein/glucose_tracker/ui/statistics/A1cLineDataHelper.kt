package com.axel_stein.glucose_tracker.ui.statistics

import android.graphics.Color
import com.axel_stein.glucose_tracker.data.model.A1cLog
import com.github.mikephil.charting.data.Entry

class A1cLineDataHelper(
    logs: List<A1cLog>,
    private var lineColor: Int = Color.BLACK,
    private var fillColor: Int = lineColor,
    private val months: Array<String>
) {
    private var maxValue = 0f
    private val entries = ArrayList<Entry>()
    private val labels = Array(logs.size) { "" }

    init {
        logs.forEachIndexed { i, log ->
            if (log.value > maxValue) {
                maxValue = log.value
            }
            val currentMonth = log.dateTime.monthOfYear-1
            labels[i] = months[currentMonth]
            entries.add(Entry(i.toFloat(), log.value))
        }
    }

    fun labels() = labels

    fun maxValue() = maxValue

    fun createLineData() = LineDataCreator(entries, lineColor, fillColor).create()
}