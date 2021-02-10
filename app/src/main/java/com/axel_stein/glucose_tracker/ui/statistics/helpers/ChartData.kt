package com.axel_stein.glucose_tracker.ui.statistics.helpers

import com.axel_stein.glucose_tracker.data.room.model.A1cLog
import com.axel_stein.glucose_tracker.data.room.model.GlucoseLog
import com.axel_stein.glucose_tracker.data.room.model.WeightLog
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.utils.round
import com.github.mikephil.charting.data.LineData
import javax.inject.Inject

class ChartData {
    private lateinit var settings: AppSettings
    private lateinit var resources: AppResources
    private lateinit var lineData: LineData
    private lateinit var labels: Array<String>
    private lateinit var limits: ArrayList<Float>
    private var maxValue = 0f

    init {
        App.appComponent.inject(this)
    }

    @Inject
    fun setSettings(s: AppSettings) {
        settings = s
    }

    @Inject
    fun setResources(r: AppResources) {
        resources = r
    }

    fun setGlucoseLogs(list: List<GlucoseLog>, type: Int) {
        limits = if (settings.useMmolAsGlucoseUnits()) {
            if (type == 0) arrayListOf(5.5f, 7f, 3.5f) else arrayListOf(7.8f, 11f, 3.5f)
        } else {
            if (type == 0) arrayListOf(100f, 126f, 70f) else arrayListOf(140f, 200f, 70f)
        }

        val items = if (type == 0) {
            list.filter { it.measured in intArrayOf(0, 2, 4, 6) }
        } else {
            list.filter { it.measured in intArrayOf(1, 3, 5) }
        }.sortedBy { it.dateTime }

        val inflater = LineDataInflater()
        inflater.inflate(items) {
            val log = it as GlucoseLog
            val value = if (settings.useMmolAsGlucoseUnits()) log.valueMmol else log.valueMg.toFloat()
            value to log.dateTime
        }

        val lineColor = if (type == 0) resources.beforeMealLineColor() else resources.afterMealLineColor()
        val fillColor = if (type == 0) resources.beforeMealFillColor() else resources.afterMealFillColor()
        createChartData(inflater, lineColor, fillColor, limits)
    }

    fun setA1cLogs(list: List<A1cLog>) {
        val inflater = LineDataInflater()
        inflater.inflate(list.sortedBy { it.dateTime }) {
            val log = it as A1cLog
            log.value to log.dateTime
        }
        val lineColor = resources.a1cLineColor()
        val fillColor = resources.a1cFillColor()
        createChartData(inflater, lineColor, fillColor, arrayListOf(6f, 7f, 8f))
    }

    fun setWeightLogs(list: List<WeightLog>) {
        val inflater = LineDataInflater()
        inflater.inflate(list.sortedBy { it.dateTime }) {
            val log = it as WeightLog
            val value = if (settings.useMetricSystem()) log.kg else log.pounds
            value.round() to log.dateTime
        }
        val lineColor = resources.weightLineColor()
        val fillColor = resources.weightFillColor()
        createChartData(inflater, lineColor, fillColor, arrayListOf())
    }

    private fun createChartData(inflater: LineDataInflater, lineColor: Int, fillColor: Int, limits: ArrayList<Float>) {
        this.limits = limits
        labels = inflater.getLabels()
        maxValue = inflater.getMaxValue()
        lineData = LineDataCreator()
            .from(inflater.getEntries(), lineColor, fillColor)
            .create()
    }

    fun isEmpty() = labels.isNullOrEmpty()

    fun getLabels() = labels

    fun getLimits() = limits

    fun getMaxValue() = maxValue

    fun getLineData() = lineData
}