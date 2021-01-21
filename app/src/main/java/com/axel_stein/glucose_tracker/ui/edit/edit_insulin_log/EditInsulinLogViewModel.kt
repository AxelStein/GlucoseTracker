package com.axel_stein.glucose_tracker.ui.edit.edit_insulin_log

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.room.model.Insulin
import com.axel_stein.glucose_tracker.data.room.model.InsulinLog
import com.axel_stein.glucose_tracker.data.room.dao.InsulinDao
import com.axel_stein.glucose_tracker.data.room.dao.InsulinLogDao
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.utils.formatIfInt
import com.axel_stein.glucose_tracker.utils.getOrDefault
import com.axel_stein.glucose_tracker.utils.hasValue
import com.axel_stein.glucose_tracker.utils.ui.Event
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import javax.inject.Inject

class EditInsulinLogViewModel(private val id: Long = 0L, private val state: SavedStateHandle) : ViewModel() {
    private val dateTime: MutableLiveData<MutableDateTime> = state.getLiveData("date_time")
    val dateTimeLiveData: LiveData<MutableDateTime> = dateTime

    private val units = MutableLiveData<String>()
    val unitsLiveData: LiveData<String> = units

    private val insulinList = MutableLiveData<List<Insulin>>()
    val insulinLiveData: LiveData<List<Insulin>> = insulinList

    private val insulinSelected = MutableLiveData<Int>()
    val insulinSelectedLiveData: LiveData<Int> = insulinSelected

    private val editorActive = MutableLiveData<Boolean>()
    val editorActiveLiveData: LiveData<Boolean> = editorActive

    private val measured = MutableLiveData<Int>()
    val measuredLiveData: LiveData<Int> = measured

    private val errorLoading = MutableLiveData<Boolean>()
    val errorLoadingLiveData: LiveData<Boolean> = errorLoading

    private val errorUnitsEmpty = MutableLiveData<Boolean>()
    val errorUnitsEmptyLiveData: LiveData<Boolean> = errorUnitsEmpty

    private val showMessage = MutableLiveData<Event<Int>>()
    val showMessageLiveData: LiveData<Event<Int>> = showMessage

    private val actionFinish = MutableLiveData<Event<Boolean>>()
    val actionFinishLiveData: LiveData<Event<Boolean>> = actionFinish

    private lateinit var logDao: InsulinLogDao
    private lateinit var dao: InsulinDao
    private val disposables = CompositeDisposable()

    init {
        App.appComponent.inject(this)
        loadData()
    }

    @Inject
    fun setDao(dao: InsulinDao, logDao: InsulinLogDao) {
        this.dao = dao
        this.logDao = logDao
    }

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

    fun selectInsulin(position: Int) {
        insulinSelected.value = position
        state["insulin_selected"] = position
    }

    fun setUnits(units: String) {
        this.units.value = units
        if (units.isNotBlank()) {
            errorUnitsEmpty.value = false
        }
    }

    fun selectMeasured(position: Int) {
        this.measured.value = position
        state["measured"] = position
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
        disposables.dispose()
    }

    @SuppressLint("CheckResult")
    private fun loadActiveInsulinList(insulinId: Long = -1L) {
        dao.getActiveItems()
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe({
                insulinList.value = it
                val restoredSelection = state.get<Int>("insulin_selected")
                if (it.isEmpty()) {
                    editorActive.value = false
                } else if (restoredSelection == null) {
                    if (insulinId == -1L) selectInsulin(0)
                    else it.forEachIndexed { index, insulin ->
                        if (insulin.id == insulinId) {
                            selectInsulin(index)
                            return@forEachIndexed
                        }
                    }
                } else {
                    selectInsulin(restoredSelection)
                }
            }, {
                it.printStackTrace()
                errorLoading.value = true
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
                    it.log.units.formatIfInt(),
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
                errorLoading.value = true
            })
    }

    private fun setData(
        dateTime: MutableDateTime = MutableDateTime.now(),
        units: String = "",
        measured: Int = 0
    ) {
        if (!this.dateTime.hasValue()) this.dateTime.value = dateTime
        this.units.value = state.get("units") ?: units
        selectMeasured(state.get("measured") ?: measured)
    }

    @SuppressLint("CheckResult")
    fun save() {
        when {
            units.value.isNullOrBlank() -> {
                errorUnitsEmpty.value = true
            }
            else -> {
                Completable.fromAction {
                    logDao.upsert(createLog())
                }.subscribeOn(io()).subscribe({
                    actionFinish.postValue(Event(true))
                }, {
                    it.printStackTrace()
                    showMessage.postValue(Event(R.string.error_saving_log))
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
                { actionFinish.postValue(Event(true)) },
                {
                    it.printStackTrace()
                    showMessage.postValue(Event(R.string.error_deleting_log))
                }
            )
        }
    }
}