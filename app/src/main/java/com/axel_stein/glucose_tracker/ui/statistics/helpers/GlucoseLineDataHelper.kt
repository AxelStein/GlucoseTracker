package com.axel_stein.glucose_tracker.ui.statistics.helpers

import android.graphics.Color
import com.axel_stein.glucose_tracker.data.model.GlucoseLog
import com.github.mikephil.charting.data.Entry

class GlucoseLineDataHelper(
    logs: List<GlucoseLog>,
    private val hypoThreshold: Float,
    private val hyperThreshold: Float,
    private var measureFilter: IntArray = intArrayOf(),
    private var lineColor: Int = Color.BLACK,
    private var fillColor: Int = lineColor,
    private val useMmol: Boolean = true,
    private val limits: ArrayList<Float>,
    private val months: Array<String>
) {
    private var maxValue = 0f
    private var hypoCount = 0
    private var hyperCount = 0
    private val entries = ArrayList<Entry>()
    private val labels = Array(logs.size) { "" }

    init {
        var currentMonth = -1
        var currentDay = -1

        logs.filter { item -> item.measured in measureFilter }
        .forEachIndexed { i, log ->
            val value = if (useMmol) log.valueMmol else log.valueMg.toFloat()
            if (value > maxValue) {
                maxValue = value
            }
            if (value < hypoThreshold) {
                hypoCount++
            }
            if (value > hyperThreshold) {
                hyperCount++
            }

            val month = log.dateTime.monthOfYear-1
            val day = log.dateTime.dayOfMonth
            if (currentMonth != month) {
                currentMonth = month
                currentDay = day
                labels[i] = "${months[month]} $day"
            } else {
                if (currentDay != day) {
                    currentDay = day
                    labels[i] = log.dateTime.dayOfMonth.toString()
                } else {
                    labels[i] = "-"
                }
            }
            entries.add(Entry(i.toFloat(), value))
        }
        limits.forEachIndexed { index, v ->
            limits[index] = if (useMmol) v else intoMgDl(v)
        }
    }

    private fun intoMgDl(mmolL: Float) = mmolL * 18f

    fun labels() = labels

    fun limits() = limits

    fun isNotEmpty() = entries.isNotEmpty()

    fun maxValue() = maxValue

    fun hypoCount() = hypoCount

    fun hyperCount() = hyperCount

    fun createLineData() = LineDataCreator(entries, lineColor, fillColor).create()
}