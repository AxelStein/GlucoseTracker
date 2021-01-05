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
import com.github.mikephil.charting.data.LineData
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class StatisticsViewModel(
    dao: StatsDao?= null,
    glucoseDao: GlucoseLogDao?= null,
    a1cDao: A1cLogDao?= null,
    appSettings: AppSettings?= null,
    appResources: AppResources?= null
): ViewModel() {
    private val data = MutableLiveData<Stats>()
    private val control = MutableLiveData<Int>()
    private var period = -1
    private val showError = MutableLiveData(false)

    private val beforeMealChart = MutableLiveData<LineData>()
    private val beforeMealMax = MutableLiveData<Float>()

    private val afterMealChart = MutableLiveData<LineData>()
    private val afterMealMax = MutableLiveData<Float>()

    private val a1cChart = MutableLiveData<LineData>()
    private val a1cMax = MutableLiveData<Float>()

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

    fun afterMealChartLiveData(): LiveData<LineData> = afterMealChart

    fun afterMealMaxLiveData(): LiveData<Float> = afterMealMax

    fun a1cChartLiveData(): LiveData<LineData> = a1cChart

    fun a1cMaxLiveData(): LiveData<Float> = a1cMax

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
            .doOnSuccess { loadCharts(period) }
            .subscribe({ stats ->
                stats.minFormatted = formatMin(stats)
                stats.maxFormatted = formatMax(stats)
                stats.avgFormatted = formatAvg(stats)
                stats.a1cFormatted = formatA1C(stats)
                data.postValue(stats)
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
                Color.parseColor("#00796B"),
                Color.parseColor("#80CBC4")
            )
            beforeMealChart.postValue(beforeMealData.createLineData())
            beforeMealMax.postValue(beforeMealData.maxValue())

            val afterMealData = GlucoseLineDataHelper(
                logs, 3.8f, 11f,
                intArrayOf(1, 3, 5),
                Color.parseColor("#1976D2"),
                Color.parseColor("#90CAF9")
            )
            afterMealChart.postValue(afterMealData.createLineData())
            afterMealMax.postValue(afterMealData.maxValue())
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
            .subscribe({
                val data = A1cLineDataHelper(
                    it.sortedBy { it.dateTime },
                    Color.parseColor("#7B1FA2"),
                    Color.parseColor("#CE93D8")
                )
                a1cChart.postValue(data.createLineData())
                a1cMax.postValue(data.maxValue())
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
        val value = if (appSettings.useMmolAsGlucoseUnits()) stats.avg_mmol else stats.avg_mg
        return "${String.format("%.1f", value.toFloat())} ${appResources.currentSuffix()}"
                .replace(',', '.')
    }

    private fun formatA1C(stats: Stats): String = "${String.format("%.1f", calcA1C(stats))}%"
            .replace(',', '.')

    private fun calcA1C(stats: Stats): Float {
        val useMmol = appSettings.useMmolAsGlucoseUnits()
        val avg = if (useMmol) stats.avg_mmol.toFloat() else stats.avg_mg.toFloat()
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