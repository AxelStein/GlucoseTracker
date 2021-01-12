package com.axel_stein.glucose_tracker.ui.edit_insulin_log

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.model.Insulin
import com.axel_stein.glucose_tracker.data.model.InsulinLog
import com.axel_stein.glucose_tracker.data.room.dao.InsulinDao
import com.axel_stein.glucose_tracker.data.room.dao.InsulinLogDao
import com.axel_stein.glucose_tracker.utils.getOrDefault
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.DateTime
import org.joda.time.MutableDateTime

open class EditInsulinLogViewModelImpl(private val id: Long = 0L) : ViewModel() {
    protected var dateTime = MutableLiveData<MutableDateTime>()
    protected var insulinList = MutableLiveData<List<Insulin>>()
    protected var insulinSelected = MutableLiveData<Int>()
    protected var units = MutableLiveData<String>()
    protected var measured = MutableLiveData<Int>()
    protected var errorLoading = MutableLiveData<Boolean>()
    protected var errorUnitsEmpty = MutableLiveData<Boolean>()
    protected var errorSave = MutableLiveData<Boolean>()
    protected var errorDelete = MutableLiveData<Boolean>()
    protected var actionFinish = MutableLiveData<Boolean>()
    protected lateinit var logDao: InsulinLogDao
    protected lateinit var listDao: InsulinDao

    fun dateTimeLiveData(): LiveData<MutableDateTime> = dateTime
    fun insulinLiveData(): LiveData<List<Insulin>> = insulinList
    fun insulinSelectedLiveData(): LiveData<Int> = insulinSelected
    fun unitsLiveData(): LiveData<String> = units
    fun measuredLiveData(): LiveData<Int> = measured
    fun errorLoadingLiveData(): LiveData<Boolean> = errorLoading
    fun errorUnitsEmptyLiveData(): LiveData<Boolean> = errorUnitsEmpty
    fun errorSaveLiveData(): LiveData<Boolean> = errorSave
    fun errorDeleteLiveData(): LiveData<Boolean> = errorDelete
    fun actionFinishLiveData(): LiveData<Boolean> = actionFinish
    fun getCurrentDateTime(): DateTime = dateTime.value?.toDateTime() ?: DateTime()

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        dateTime.postValue(dateTime.value.apply {
            this?.year = year
            this?.monthOfYear = month
            this?.dayOfMonth = dayOfMonth
        })
    }

    fun setTime(hourOfDay: Int, minuteOfHour: Int) {
        dateTime.postValue(dateTime.value.apply {
            this?.hourOfDay = hourOfDay
            this?.minuteOfHour = minuteOfHour
        })
    }

    open fun selectInsulin(position: Int) {
        insulinSelected.value = position
    }

    fun setUnits(units: String) {
        this.units.value = units
        if (units.isNotBlank()) {
            errorUnitsEmpty.value = false
        }
    }

    fun selectMeasured(position: Int) {
        this.measured.value = position
    }

    @SuppressLint("CheckResult")
    private fun loadInsulinList(callback: (List<Insulin>) -> Unit) {
        listDao.getItems().subscribeOn(io()).doOnSuccess { callback(it) }.subscribe({ items ->
            insulinList.postValue(items)
        }, {
            it.printStackTrace()
        })
    }

    @SuppressLint("CheckResult")
    fun loadData() {
        loadInsulinList { insulinItems ->
            if (id != 0L) {
                logDao.get(id).subscribe({ log ->
                    postData(
                        log.dateTime.toMutableDateTime(),
                        log.units.toString(),
                        log.measured,
                        if (insulinItems.isNotEmpty()) {
                            insulinItems.indexOf(
                                insulinItems.find { item -> item.id == log.id }
                            )
                        } else {
                            -1
                        }
                    )
                }, {
                    it.printStackTrace()
                })
            } else {
                postData(insulinSelected = if (insulinItems.isNotEmpty()) 0 else -1)
            }
        }
    }

    private fun postData(
        dateTime: MutableDateTime = MutableDateTime.now(),
        units: String = "",
        measured: Int = 0,
        insulinSelected: Int = -1
    ) {
        this.dateTime.postValue(dateTime)
        this.units.postValue(units)
        this.measured.postValue(measured)
        if (insulinSelected > -1) {
            this.insulinSelected.postValue(insulinSelected)
        }
    }

    @SuppressLint("CheckResult")
    fun save() {
        if (units.value.isNullOrBlank()) {
            errorUnitsEmpty.value = true
        } else {
            createLog()
                .subscribeOn(io())
                .map { log ->
                    if (id != 0L) logDao.update(log)
                    else logDao.insert(log)
                }
                .subscribe(
                    { actionFinish.postValue(true) },
                    { it.printStackTrace() }
                )
        }
    }

    private fun createLog() = Single.fromCallable {
        val items = insulinList.getOrDefault(emptyList())
        if (items.isEmpty()) {
            throw IllegalStateException("Insulin list is empty")
        }
        val insulin = items[insulinSelected.getOrDefault(0)]
        InsulinLog(
            insulin.id,
            units.getOrDefault("").toFloat(),
            measured.getOrDefault(0),
            dateTime.getOrDefault(MutableDateTime()).toDateTime()
        )
    }

    @SuppressLint("CheckResult")
    fun delete() {
        if (id != 0L) {
            logDao.deleteById(id).subscribeOn(io()).subscribe(
                { actionFinish.postValue(true) },
                { it.printStackTrace() }
            )
        }
    }
}

