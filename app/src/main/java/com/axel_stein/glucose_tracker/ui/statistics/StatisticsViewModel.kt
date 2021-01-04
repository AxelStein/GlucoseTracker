package com.axel_stein.glucose_tracker.ui.statistics

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.room.dao.GlucoseLogDao
import com.axel_stein.glucose_tracker.data.room.dao.StatsDao
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.data.stats.Stats
import com.axel_stein.glucose_tracker.ui.App
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class StatisticsViewModel(
    dao: StatsDao?= null,
    glucoseDao: GlucoseLogDao?= null,
    appSettings: AppSettings?= null,
    appResources: AppResources?= null
): ViewModel() {
    private val data = MutableLiveData<Stats>()
    private val control = MutableLiveData<Int>()
    private var period = -1
    private val showError = MutableLiveData(false)

    @Inject
    lateinit var dao: StatsDao

    @Inject
    lateinit var glucoseDao: GlucoseLogDao

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
            if (appSettings != null) {
                this.appSettings = appSettings
            }
            if (appResources != null) {
                this.appResources = appResources
            }
        }
        setPeriod(0)
    }

    fun statsLiveData(): LiveData<Stats> = data

    fun diabetesControlLiveData(): LiveData<Int> = control

    fun showErrorLiveData(): LiveData<Boolean> = showError

    @SuppressLint("CheckResult")
    fun setPeriod(p: Int) {
        if (period == p) {
            return
        }
        period = p

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
            .subscribeOn(Schedulers.io())
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