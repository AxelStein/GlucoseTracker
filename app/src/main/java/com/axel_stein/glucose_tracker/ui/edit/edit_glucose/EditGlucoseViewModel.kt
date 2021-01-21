package com.axel_stein.glucose_tracker.ui.edit.edit_glucose

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.room.model.GlucoseLog
import com.axel_stein.glucose_tracker.data.room.dao.GlucoseLogDao
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.utils.getOrDefault
import com.axel_stein.glucose_tracker.utils.intoMgDl
import com.axel_stein.glucose_tracker.utils.intoMmol
import com.axel_stein.glucose_tracker.utils.round
import com.axel_stein.glucose_tracker.utils.ui.Event
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import javax.inject.Inject
import kotlin.math.absoluteValue

class EditGlucoseViewModel(private val id: Long = 0L, private val state: SavedStateHandle) : ViewModel() {
    private val dateTime = MutableLiveData<MutableDateTime>()
    val dateTimeLiveData: LiveData<MutableDateTime> = dateTime

    private val glucose = MutableLiveData<String>()
    val glucoseLiveData: LiveData<String> = glucose

    private val measured = MutableLiveData<Int>()
    val measuredLiveData: LiveData<Int> = measured

    private val errorLoading = MutableLiveData<Boolean>()
    val errorLoadingLiveData: LiveData<Boolean> = errorLoading

    private val errorGlucoseEmpty = MutableLiveData<Boolean>()
    val errorGlucoseEmptyLiveData: LiveData<Boolean> = errorGlucoseEmpty

    private val actionFinish = MutableLiveData<Event<Boolean>>()
    val actionFinishLiveData: LiveData<Event<Boolean>> = actionFinish

    private val showMessage = MutableLiveData<Event<Int>>()
    val showMessageLiveData: LiveData<Event<Int>> = showMessage

    private var useMmol = true
    private lateinit var dao: GlucoseLogDao
    private lateinit var settings: AppSettings
    private lateinit var resources: AppResources

    init {
        App.appComponent.inject(this)
        loadData()
    }

    @Inject
    fun setDao(dao: GlucoseLogDao) {
        this.dao = dao
    }

    @Inject
    fun setSettings(settings: AppSettings) {
        this.settings = settings
        useMmol = this.settings.useMmolAsGlucoseUnits()
    }

    @Inject
    fun setResources(resources: AppResources) {
        this.resources = resources
    }

    fun getCurrentDateTime(): DateTime = dateTime.getOrDefault(MutableDateTime()).toDateTime()

    private fun glucoseValueMmol() = glucose.getOrDefault("0").toFloat().absoluteValue

    private fun glucoseValueMg() = glucose.getOrDefault("0").toInt().absoluteValue

    fun loadData() {
        if (id == 0L) setData()
        else Single.fromCallable { dao.getById(id) }
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe({ log ->
                val logGlucose = if (useMmol) log.valueMmol else log.valueMg
                setData(
                    log.dateTime.toMutableDateTime(),
                    logGlucose.toString(),
                    log.measured
                )
            }, {
                it.printStackTrace()
                errorLoading.postValue(true)
            })
    }

    private fun setData(dateTime: MutableDateTime = MutableDateTime(), glucose: String = "", measured: Int = 0) {
        this.dateTime.value = state.get("date_time") ?: dateTime
        this.glucose.value = state.get("glucose") ?: glucose
        this.measured.value = state.get("measured") ?: measured
    }

    @SuppressLint("CheckResult")
    fun save() {
        if (glucose.value.isNullOrEmpty()) {
            errorGlucoseEmpty.value = true
        } else {
            Completable.fromAction { dao.upsert(createLog()) }
                .subscribeOn(io())
                .subscribe({
                    actionFinish.postValue(Event())
                }, {
                    it.printStackTrace()
                    showMessage.postValue(Event(R.string.error_saving_log))
                })
        }
    }

    private fun createLog(): GlucoseLog {
        val mmol = (if (useMmol) glucoseValueMmol() else glucoseValueMg().intoMmol()).round()
        val mg = if (useMmol) glucoseValueMmol().intoMgDl() else glucoseValueMg()
        return GlucoseLog(mmol, mg, measured.getOrDefault(0), getCurrentDateTime()).also { it.id = id }
    }

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        dateTime.postValue(dateTime.value.apply {
            this?.year = year
            this?.monthOfYear = month
            this?.dayOfMonth = dayOfMonth
        })
        state["date_time"] = dateTime.value
    }

    fun setTime(hourOfDay: Int, minuteOfHour: Int) {
        dateTime.postValue(dateTime.value.apply {
            this?.hourOfDay = hourOfDay
            this?.minuteOfHour = minuteOfHour
        })
        state["date_time"] = dateTime.value
    }

    fun setGlucose(value: String) {
        glucose.value = value.replace(',', '.')
        if (value.isNotEmpty()) {
            errorGlucoseEmpty.value = false
        }
        state["glucose"] = value
    }

    fun setMeasured(measured: Int) {
        val maxMeasured = resources.measuredArray.size - 1

        this.measured.value = when {
            measured < 0 -> 0
            measured > maxMeasured -> maxMeasured
            else -> measured
        }
        state["measured"] = this.measured.value
    }

    @SuppressLint("CheckResult")
    fun delete() {
        if (id != 0L) Completable.fromAction { dao.deleteById(id) }
            .subscribeOn(io())
            .subscribe({
                actionFinish.postValue(Event())
            }, {
                it.printStackTrace()
                showMessage.postValue(Event(R.string.error_deleting_log))
            })
    }
}