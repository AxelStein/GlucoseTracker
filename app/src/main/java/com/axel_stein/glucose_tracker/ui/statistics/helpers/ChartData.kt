package com.axel_stein.glucose_tracker.ui.statistics.helpers

import com.axel_stein.glucose_tracker.data.model.A1cLog
import com.axel_stein.glucose_tracker.data.model.GlucoseLog
import com.axel_stein.glucose_tracker.data.model.PulseLog
import com.axel_stein.glucose_tracker.data.model.WeightLog
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.App
import com.github.mikephil.charting.data.LineData
import javax.inject.Inject

class ChartData {
    private lateinit var settings: AppSettings
    private lateinit var resources: AppResources
    private var lineColor: Int = 0
    private var fillColor: Int = 0
    private var limits = ArrayList<Float>()
    private lateinit var inflater: DateLabelInflater
    private lateinit var lineData: LineData

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
        lineColor = if (type == 0) resources.beforeMealLineColor() else resources.afterMealLineColor()
        fillColor = if (type == 0) resources.beforeMealFillColor() else resources.afterMealFillColor()
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

        inflater = DateLabelInflater()
        inflater.inflate(items) {
            val log = it as GlucoseLog
            val value = if (settings.useMmolAsGlucoseUnits()) log.valueMmol else log.valueMg.toFloat()
            value to log.dateTime
        }
        createLineData()
    }

    fun setA1cLogs(list: List<A1cLog>) {
        lineColor = resources.a1cLineColor()
        fillColor = resources.a1cFillColor()
        limits = arrayListOf(6f, 7f, 8f)

        inflater = DateLabelInflater()
        inflater.inflate(list.sortedBy { it.dateTime }) {
            val log = it as A1cLog
            log.value to log.dateTime
        }
        createLineData()
    }

    fun setWeightLogs(list: List<WeightLog>) {
        lineColor = resources.weightLineColor()
        fillColor = resources.weightFillColor()
        limits = arrayListOf()

        inflater = DateLabelInflater()
        inflater.inflate(list.sortedBy { it.dateTime }) {
            val log = it as WeightLog
            log.kg to log.dateTime
        }
        createLineData()
    }

    fun setPulseLogs(list: List<PulseLog>) {
        lineColor = resources.pulseLineColor()
        fillColor = resources.pulseFillColor()
        limits = arrayListOf(60f, 90f)

        inflater = DateLabelInflater()
        inflater.inflate(list.sortedBy { it.dateTime }) {
            val log = it as PulseLog
            log.value.toFloat() to log.dateTime
        }
        createLineData()
    }

    private fun createLineData() {
        lineData = LineDataCreator(inflater.getEntries(), lineColor, fillColor).create()
    }

    fun isEmpty() = inflater.getEntries().isNullOrEmpty()

    fun getLabels() = inflater.getLabels()

    fun getLimits() = limits

    fun getMaxValue() = inflater.getMaxValue()

    fun getLineData() = lineData
}