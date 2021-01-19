package com.axel_stein.glucose_tracker.ui.statistics

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.room.dao.A1cLogDao
import com.axel_stein.glucose_tracker.data.room.dao.GlucoseLogDao
import com.axel_stein.glucose_tracker.data.room.dao.StatsDao
import com.axel_stein.glucose_tracker.data.room.dao.WeightLogDao
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.data.stats.Stats
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.ui.statistics.helpers.ChartData
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class StatisticsViewModel : ViewModel() {
    private lateinit var statsDao: StatsDao
    private lateinit var glucoseDao: GlucoseLogDao
    private lateinit var a1cDao: A1cLogDao
    private lateinit var weightDao: WeightLogDao
    private lateinit var settings: AppSettings
    private val disposables = CompositeDisposable()

    private var period = -1
    private var chartPeriod = -1
    private var chartType = -1

    private val statsData = MutableLiveData<Stats>()
    val statsLiveData: LiveData<Stats> = statsData

    private val control = MutableLiveData<Int>()
    val diabetesControlLiveData: LiveData<Int> = control

    private val showError = MutableLiveData(false)
    val showErrorLiveData: LiveData<Boolean> = showError

    private val chart = MutableLiveData<ChartData>()
    val chartLiveData: LiveData<ChartData> = chart

    init {
        App.appComponent.inject(this)
        setStatsPeriod(0)
        disposables.add(
            settings.observeGlucoseUnits()
                .subscribe {
                    forceUpdate()
                }
        )
    }

    @Inject
    fun setStatsDao(dao: StatsDao) {
        statsDao = dao
    }

    @Inject
    fun setGlucoseDao(dao: GlucoseLogDao) {
        glucoseDao = dao
    }

    @Inject
    fun setA1cDao(dao: A1cLogDao) {
        a1cDao = dao
    }

    @Inject
    fun setWeightDao(dao: WeightLogDao) {
        weightDao = dao
    }

    @Inject
    fun setSettings(settings: AppSettings) {
        this.settings = settings
    }

    @SuppressLint("CheckResult")
    fun setStatsPeriod(p: Int) {
        if (period != p) {
            period = p
            loadStats(period)
        }
    }

    private fun forceUpdate() {
        loadStats(period)
        setChartPeriod(chartPeriod)
    }

    @SuppressLint("CheckResult")
    private fun loadStats(period: Int) {
        Single.fromCallable { glucoseDao.getAll() }
            .flatMapMaybe {
                if (it.isNullOrEmpty()) {
                    Maybe.empty()
                } else {
                    Maybe.fromSingle(when (period) {
                        0 -> statsDao.twoWeeks()
                        1 -> statsDao.month()
                        else -> statsDao.threeMonths()
                    })
                }
            }
            .subscribeOn(io())
            .subscribe({ stats ->
                if (stats.min_mmol == null) {
                    statsData.postValue(null)
                } else {
                    stats.minFormatted = formatMin(stats)
                    stats.maxFormatted = formatMax(stats)
                    stats.avgFormatted = formatAvg(stats)
                    stats.a1cFormatted = formatA1C(stats)
                    statsData.postValue(stats)
                }
                showError.postValue(false)
            }, {
                it.printStackTrace()
                showError.postValue(true)
            }, {
                statsData.postValue(null)
                showError.postValue(false)
            })
    }

    private fun formatMin(stats: Stats): String {
        val value = if (settings.useMmolAsGlucoseUnits()) stats.min_mmol else stats.min_mg
        return value.toString()
    }

    private fun formatMax(stats: Stats): String {
        val value = if (settings.useMmolAsGlucoseUnits()) stats.max_mmol else stats.max_mg
        return value.toString()
    }

    private fun formatAvg(stats: Stats): String {
        val value = (if (settings.useMmolAsGlucoseUnits()) stats.avg_mmol else stats.avg_mg) ?: return ""
        return String.format("%.1f", value.toFloat()).replace(',', '.')
    }

    private fun formatA1C(stats: Stats): String = "${String.format("%.1f", calcA1C(stats))}%"
        .replace(',', '.')

    private fun calcA1C(stats: Stats): Float {
        val useMmol = settings.useMmolAsGlucoseUnits()
        val value = (if (settings.useMmolAsGlucoseUnits()) stats.avg_mmol else stats.avg_mg) ?: return 0f
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

    fun setChartPeriod(period: Int) {
        chartPeriod = period
        setChartType(chartType)
    }

    fun setChartType(type: Int) {
        chartType = type
        when (type) {
            0 -> loadGlucoseChartData(chartPeriod, 0)
            1 -> loadGlucoseChartData(chartPeriod, 1)
            2 -> loadA1cChartData()
            3 -> loadWeightChartData()
        }
    }

    @SuppressLint("CheckResult")
    private fun loadGlucoseChartData(period: Int, type: Int) {
        if (period == -1) return
        Single.fromCallable {
            when (period) {
                0 -> glucoseDao.getLastTwoWeeks()
                1 -> glucoseDao.getLastMonth()
                else -> glucoseDao.getLastThreeMonths()
            }
        }.subscribeOn(io()).subscribe({
            val data = ChartData()
            data.setGlucoseLogs(it, type)
            if (data.isEmpty()) chart.postValue(null)
            else chart.postValue(data)
        }, {
            it.printStackTrace()
        })
    }

    @SuppressLint("CheckResult")
    private fun loadA1cChartData() {
        Single.fromCallable { a1cDao.getByThisYear() }
            .subscribeOn(io())
            .subscribe({
                val data = ChartData()
                data.setA1cLogs(it)
                if (data.isEmpty()) chart.postValue(null)
                else chart.postValue(data)
            }, {
                it.printStackTrace()
            })
    }

    @SuppressLint("CheckResult")
    private fun loadWeightChartData() {
        Single.fromCallable { weightDao.getByThisYear() }
            .subscribeOn(io())
            .subscribe({
                val data = ChartData()
                data.setWeightLogs(it)
                if (data.isEmpty()) chart.postValue(null)
                else chart.postValue(data)
            }, {
                it.printStackTrace()
            })
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}