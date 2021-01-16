package com.axel_stein.glucose_tracker.ui.statistics.helpers

import android.graphics.Color
import com.axel_stein.glucose_tracker.data.model.WeightLog
import com.github.mikephil.charting.data.Entry

class WeightLineDataHelper(
    logs: List<WeightLog>,
    private var lineColor: Int = Color.BLACK,
    private var fillColor: Int = lineColor,
    months: Array<String>
) {
    private var maxValue = 0f
    private val entries = ArrayList<Entry>()
    private val dateLabelInflater = DateLabelInflater(logs.size, months)

    init {
        logs.forEachIndexed { i, log ->
            if (log.kg > maxValue) {
                maxValue = log.kg
            }
            dateLabelInflater.add(i, log.dateTime)
            entries.add(Entry(i.toFloat(), log.kg))
        }
    }

    fun labels() = dateLabelInflater.labels()

    fun maxValue() = maxValue

    fun createLineData() = LineDataCreator(entries, lineColor, fillColor).create()
}