package com.axel_stein.glucose_tracker.ui.statistics

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.room.dao.A1cLogDao
import com.axel_stein.glucose_tracker.data.room.dao.GlucoseLogDao
import com.axel_stein.glucose_tracker.data.room.dao.StatsDao
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.data.stats.Stats
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.ui.statistics.helpers.A1cLineDataHelper
import com.axel_stein.glucose_tracker.ui.statistics.helpers.ChartColors
import com.axel_stein.glucose_tracker.ui.statistics.helpers.GlucoseLineDataHelper
import com.github.mikephil.charting.data.LineData
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class StatisticsViewModel(
    dao: StatsDao? = null,
    glucoseDao: GlucoseLogDao? = null,
    a1cDao: A1cLogDao? = null,
    appSettings: AppSettings? = null,
    appResources: AppResources? = null,
    private val chartColors: ChartColors? = null
): ViewModel() {
    private val data = MutableLiveData<Stats>()
    private val control = MutableLiveData<Int>()
    private var period = -1
    private val showError = MutableLiveData(false)

    private val beforeMealChart = MutableLiveData<LineData>()
    private val beforeMealMax = MutableLiveData<Float>()
    private val beforeMealLimits = MutableLiveData<ArrayList<Float>>()
    private val beforeMealLabels = MutableLiveData<Array<String>>()

    private val afterMealChart = MutableLiveData<LineData>()
    private val afterMealMax = MutableLiveData<Float>()
    private val afterMealLimits = MutableLiveData<ArrayList<Float>>()
    private val afterMealLabels = MutableLiveData<Array<String>>()

    private val a1cChart = MutableLiveData<LineData>()
    private val a1cMax = MutableLiveData<Float>()
    private val a1cLabels = MutableLiveData<Array<String>>()

    @Inject
    lateinit var dao: StatsDao

    @Inject
    lateinit var glucoseDao: GlucoseLogDao

    @Inject
    lateinit var a1cDao: A1cLogDao

    @Inject
    lateinit var appSettings: AppSettings

    @Inject
    lateinit var appResources: AppResources

    init {
        if (dao == null) {
            App.appComponent.inject(this)
        } else {
            this.dao = dao
            if (glucoseDao != null) {
                this.glucoseDao = glucoseDao
            }
            if (a1cDao != null) {
                this.a1cDao = a1cDao
            }
            if (appSettings != null) {
                this.appSettings = appSettings
            }
            if (appResources != null) {
                this.appResources = appResources
            }
        }
        setPeriod(0)
        loadA1c()
    }

    fun statsLiveData(): LiveData<Stats> = data

    fun diabetesControlLiveData(): LiveData<Int> = control

    fun showErrorLiveData(): LiveData<Boolean> = showError

    fun beforeMealChartLiveData(): LiveData<LineData> = beforeMealChart

    fun beforeMealMaxLiveData(): LiveData<Float> = beforeMealMax

    fun beforeMealLimitsLiveData(): LiveData<ArrayList<Float>> = beforeMealLimits

    fun beforeMealLabelsLiveData(): LiveData<Array<String>> = beforeMealLabels

    fun afterMealChartLiveData(): LiveData<LineData> = afterMealChart

    fun afterMealMaxLiveData(): LiveData<Float> = afterMealMax

    fun afterMealLimitsLiveData(): LiveData<ArrayList<Float>> = afterMealLimits

    fun afterMealLabelsLiveData(): LiveData<Array<String>> = afterMealLabels

    fun a1cChartLiveData(): LiveData<LineData> = a1cChart

    fun a1cMaxLiveData(): LiveData<Float> = a1cMax

    fun a1cLabelsLiveData(): LiveData<Array<String>> = a1cLabels

    fun axisMaximum(v: Float): Float {
        return if(appSettings.useMmolAsGlucoseUnits()) {
            if (v < 10f) 10f else v + 2f
        } else {
            if (v < 180f) 180f else v + 40f
        }
    }

    @SuppressLint("CheckResult")
    fun setPeriod(p: Int) {
        if (period != p) {
            period = p
            loadStats(period)
        }
    }

    @SuppressLint("CheckResult")
    private fun loadStats(period: Int) {
        Single.fromCallable { glucoseDao.get() }
            .flatMapMaybe {
                if (it.isNullOrEmpty()) {
                    Maybe.empty()
                } else {
                    Maybe.fromSingle(when (period) {
                        0 -> dao.twoWeeks()
                        1 -> dao.month()
                        else -> dao.threeMonths()
                    })
                }
            }
            .subscribeOn(io())
            .subscribe({ stats ->
                if (stats.min_mmol == null) {
                    data.postValue(null)
                } else {
                    stats.minFormatted = formatMin(stats)
                    stats.maxFormatted = formatMax(stats)
                    stats.avgFormatted = formatAvg(stats)
                    stats.a1cFormatted = formatA1C(stats)
                    data.postValue(stats)
                }
                loadCharts(period)
                showError.postValue(false)
            }, {
                it.printStackTrace()
                showError.postValue(true)
            }, {
                data.postValue(null)
                showError.postValue(false)
            })
    }

    @SuppressLint("CheckResult")
    private fun loadCharts(period: Int) {
        when (period) {
            0 -> glucoseDao.getLastTwoWeeks()
            1 -> glucoseDao.getLastMonth()
            else -> glucoseDao.getLastThreeMonths()
        }.subscribeOn(io()).subscribe({
            val logs = it.sortedBy { item -> item.dateTime }

            val beforeMealData = GlucoseLineDataHelper(
                logs, 3.8f, 7f,
                intArrayOf(0, 2, 4, 6),
                chartColors?.beforeMealLineColor ?: Color.BLACK,
                chartColors?.beforeMealFillColor ?: Color.BLACK,
                appSettings.useMmolAsGlucoseUnits(),
                arrayListOf(5.5f, 7f, 3.5f),
                appResources.monthsAbbrArray()
            )
            if (beforeMealData.isNotEmpty()) {
                beforeMealChart.postValue(beforeMealData.createLineData())
                beforeMealMax.postValue(beforeMealData.maxValue())
                beforeMealLimits.postValue(beforeMealData.limits())
                beforeMealLabels.postValue(beforeMealData.labels())
            } else {
                beforeMealChart.postValue(null)
            }

            val afterMealData = GlucoseLineDataHelper(
                logs, 3.8f, 11f,
                intArrayOf(1, 3, 5),
                chartColors?.afterMealLineColor ?: Color.BLACK,
                chartColors?.afterMealFillColor ?: Color.BLACK,
                appSettings.useMmolAsGlucoseUnits(),
                arrayListOf(7.8f, 11f, 3.5f),
                appResources.monthsAbbrArray()
            )
            if (afterMealData.isNotEmpty()) {
                afterMealChart.postValue(afterMealData.createLineData())
                afterMealMax.postValue(afterMealData.maxValue())
                afterMealLimits.postValue(afterMealData.limits())
                afterMealLabels.postValue(afterMealData.labels())
            } else {
                afterMealChart.postValue(null)
            }
        }, {
            it.printStackTrace()
        })
    }

    @SuppressLint("CheckResult")
    private fun loadA1c() {
        Single.fromCallable { a1cDao.getThisYear() }
            .flatMapMaybe {
                if (it.isNotEmpty()) {
                    Maybe.just(it)
                } else {
                    Maybe.empty()
                }
            }
            .subscribeOn(io())
            .subscribe({ logs ->
                val data = A1cLineDataHelper(
                    logs.sortedBy { it.dateTime },
                    chartColors?.a1cLineColor ?: Color.BLACK,
                    chartColors?.a1cFillColor ?: Color.BLACK,
                    appResources.monthsAbbrArray()
                )
                a1cChart.postValue(data.createLineData())
                a1cMax.postValue(data.maxValue())
                a1cLabels.postValue(data.labels())
            }, {
                it.printStackTrace()
            })
    }

    private fun formatMin(stats: Stats): String {
        val value = if (appSettings.useMmolAsGlucoseUnits()) stats.min_mmol else stats.min_mg
        return "$value ${appResources.currentSuffix()}"
    }

    private fun formatMax(stats: Stats): String {
        val value = if (appSettings.useMmolAsGlucoseUnits()) stats.max_mmol else stats.max_mg
        return "$value ${appResources.currentSuffix()}"
    }

    private fun formatAvg(stats: Stats): String {
        val value = (if (appSettings.useMmolAsGlucoseUnits()) stats.avg_mmol else stats.avg_mg) ?: return ""
        return "${String.format("%.1f", value.toFloat())} ${appResources.currentSuffix()}"
                .replace(',', '.')
    }

    private fun formatA1C(stats: Stats): String = "${String.format("%.1f", calcA1C(stats))}%"
            .replace(',', '.')

    private fun calcA1C(stats: Stats): Float {
        val useMmol = appSettings.useMmolAsGlucoseUnits()
        val value = (if (appSettings.useMmolAsGlucoseUnits()) stats.avg_mmol else stats.avg_mg) ?: return 0f
        val avg = value.toFloat()
        val a1c = if (useMmol) {
            (avg + 2.59f) / 1.59f
        } else {
            (avg + 46.7f) / 28.7f
        }
        control.postValue(
            when {
                a1c < 7f -> 0
                a1c in 7f..8f -> 1
                else -> 2
            }
        )
        return a1c
    }
}