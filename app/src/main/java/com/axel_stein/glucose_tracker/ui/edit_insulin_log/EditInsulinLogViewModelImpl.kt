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
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.DateTime
import org.joda.time.MutableDateTime

open class EditInsulinLogViewModelImpl(private val id: Long = 0L) : ViewModel() {
    protected var dateTime = MutableLiveData<MutableDateTime>()
    protected var insulinList = MutableLiveData<List<Insulin>>()
    protected var insulinSelected = MutableLiveData<Int>()
    protected var editorActive = MutableLiveData<Boolean>()
    protected var units = MutableLiveData<String>()
    protected var measured = MutableLiveData<Int>()
    protected var errorLoading = MutableLiveData<Boolean>()
    protected var errorInsulinListEmpty = MutableLiveData<Boolean>()
    protected var errorUnitsEmpty = MutableLiveData<Boolean>()
    protected var errorSave = MutableLiveData<Boolean>()
    protected var errorDelete = MutableLiveData<Boolean>()
    protected var actionFinish = MutableLiveData<Boolean>()
    protected lateinit var logDao: InsulinLogDao
    protected lateinit var listDao: InsulinDao
    private val disposables = CompositeDisposable()

    fun dateTimeLiveData(): LiveData<MutableDateTime> = dateTime
    fun insulinLiveData(): LiveData<List<Insulin>> = insulinList
    fun insulinSelectedLiveData(): LiveData<Int> = insulinSelected
    fun editorActiveLiveData(): LiveData<Boolean> = editorActive
    fun unitsLiveData(): LiveData<String> = units
    fun measuredLiveData(): LiveData<Int> = measured
    fun errorLoadingLiveData(): LiveData<Boolean> = errorLoading
    fun errorUnitsEmptyLiveData(): LiveData<Boolean> = errorUnitsEmpty
    fun errorInsulinListEmptyLiveData(): LiveData<Boolean> = errorInsulinListEmpty
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

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
        disposables.dispose()
    }

    @SuppressLint("CheckResult")
    private fun loadActiveInsulinList(insulinId: Long = -1L) {
        listDao.getItems()
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe({
                insulinList.value = it
                if (it.isEmpty()) {
                    errorInsulinListEmpty.value = true
                } else {
                    if (insulinId == -1L) selectInsulin(0)
                    else it.forEachIndexed { index, insulin ->
                        if (insulin.id == insulinId) {
                            selectInsulin(index)
                            return@forEachIndexed
                        }
                    }
                }
            }, {
                it.printStackTrace()
            })
    }

    @SuppressLint("CheckResult")
    fun loadData() {
        if (id == 0L) {
            setData()
            loadActiveInsulinList()
        } else logDao.getById(id)
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe({
                setData(
                    it.log.dateTime.toMutableDateTime(),
                    it.log.units.toString(),
                    it.log.measured
                )
                if (!it.insulin.active) {
                    insulinList.value = listOf(it.insulin)
                    selectInsulin(0)
                    editorActive.value = false
                } else {
                    loadActiveInsulinList(it.insulin.id)
                }
            }, {
                it.printStackTrace()
            })
    }

    private fun setData(
        dateTime: MutableDateTime = MutableDateTime.now(),
        units: String = "",
        measured: Int = 0
    ) {
        this.dateTime.value = dateTime
        this.units.value = units
        this.measured.value = measured
    }

    @SuppressLint("CheckResult")
    fun save() {
        when {
            insulinList.value.isNullOrEmpty() -> {
                errorInsulinListEmpty.value = true
            }
            units.value.isNullOrBlank() -> {
                errorUnitsEmpty.value = true
            }
            else -> {
                Completable.fromAction {
                    logDao.upsert(createLog())
                }.subscribeOn(io()).subscribe({
                    actionFinish.postValue(true)
                }, {
                    it.printStackTrace()
                })
            }
        }
    }

    private fun createLog(): InsulinLog {
        val items = insulinList.getOrDefault(emptyList())
        if (items.isEmpty()) {
            throw IllegalStateException("Insulin list is empty")
        }
        val insulin = items[insulinSelected.getOrDefault(0)]
        return InsulinLog(
            insulin.id,
            units.getOrDefault("").toFloat(),
            measured.getOrDefault(0),
            dateTime.getOrDefault(MutableDateTime()).toDateTime()
        ).also { it.id = id }
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

