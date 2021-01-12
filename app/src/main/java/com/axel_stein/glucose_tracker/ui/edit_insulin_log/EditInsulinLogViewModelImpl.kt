package com.axel_stein.glucose_tracker.ui.edit_insulin_log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.joda.time.DateTime
import org.joda.time.MutableDateTime

open class EditInsulinLogViewModelImpl(private val id: Long = 0L) : ViewModel() {
    protected var dateTime = MutableLiveData<MutableDateTime>()
    protected var insulinList = MutableLiveData<List<String>>()
    protected var insulinSelected = MutableLiveData<Int>()
    protected var units = MutableLiveData<String>()
    protected var measured = MutableLiveData<Int>()
    protected var errorLoading = MutableLiveData<Boolean>()
    protected var errorUnitsEmpty = MutableLiveData<Boolean>()
    protected var errorSave = MutableLiveData<Boolean>()
    protected var errorDelete = MutableLiveData<Boolean>()
    protected var actionFinish = MutableLiveData<Boolean>()

    init {
        loadData()
    }

    fun dateTimeLiveData(): LiveData<MutableDateTime> = dateTime
    fun insulinLiveData(): LiveData<List<String>> = insulinList
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

    fun loadData() {
        // todo
        dateTime.value = MutableDateTime()
        insulinList.value = listOf("Actrapide (intermediate)", "Humalog (rapid)")
        insulinSelected.value = 0
        units.value = ""
        measured.value = 0
    }

    fun save() {
        if (units.value.isNullOrBlank()) {
            errorUnitsEmpty.value = true
        } else {
            // todo save
            actionFinish.value = true
        }
    }

    fun delete() {
        if (id != 0L) {
            // todo delete
            actionFinish.value = true
        } else {
            errorDelete.value = true
        }
    }
}