package com.axel_stein.glucose_tracker.ui.edit_ap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.model.ApLog
import com.axel_stein.glucose_tracker.data.room.dao.ApLogDao
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.utils.DateTimeProvider
import com.axel_stein.glucose_tracker.utils.getOrDateTime
import com.axel_stein.glucose_tracker.utils.getOrDefault
import com.axel_stein.glucose_tracker.utils.ui.Event
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.MutableDateTime
import javax.inject.Inject

class EditApViewModel(private val id: Long = 0L, private val state: SavedStateHandle) : ViewModel(), DateTimeProvider {
    private var dateTime = MutableLiveData<MutableDateTime>()

    private val systolic = MutableLiveData<String>()
    val systolicLiveData: LiveData<String> = systolic

    private val diastolic = MutableLiveData<String>()
    val diastolicLiveData: LiveData<String> = diastolic

    private val errorSystolicEmpty = MutableLiveData<Boolean>()
    val errorSystolicEmptyLiveData: LiveData<Boolean> = errorSystolicEmpty

    private val errorDiastolicEmpty = MutableLiveData<Boolean>()
    val errorDiastolicEmptyLiveData: LiveData<Boolean> = errorDiastolicEmpty

    private val showMessage = MutableLiveData<Event<Int>>()
    val showMessageLiveData: LiveData<Event<Int>> = showMessage

    private val actionFinish = MutableLiveData<Event<Boolean>>()
    val actionFinishLiveData: LiveData<Event<Boolean>> = actionFinish

    private lateinit var dao: ApLogDao

    init {
        App.appComponent.inject(this)
        loadData()
    }

    @Inject
    fun setDao(dao: ApLogDao) {
        this.dao = dao
    }

    override fun dateTimeLiveData(): LiveData<MutableDateTime> = dateTime

    override fun onDateSet(year: Int, month: Int, dayOfMonth: Int) {
        val dt = dateTime.getOrDefault()
        dateTime.value = dt.apply {
            this.year = year
            this.monthOfYear = month
            this.dayOfMonth = dayOfMonth
        }
        state["date_time"] = dateTime.value
    }

    override fun onTimeSet(hourOfDay: Int, minuteOfHour: Int) {
        val dt = dateTime.getOrDefault()
        dateTime.value = dt.apply {
            this.hourOfDay = hourOfDay
            this.minuteOfHour= minuteOfHour
        }
        state["date_time"] = dateTime.value
    }

    fun setSystolic(value: String) {
        systolic.value = value
        state["systolic"] = value
        if (value.isNotBlank()) {
            errorSystolicEmpty.value = false
        }
    }

    fun setDiastolic(value: String) {
        diastolic.value = value
        state["diastolic"] = value
        if (value.isNotBlank()) {
            errorDiastolicEmpty.value = false
        }
    }

    fun loadData() {
        if (id == 0L) setData()
        else dao.getById(id)
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe({ log ->
                setData(
                    log.dateTime.toMutableDateTime(),
                    log.systolic.toString(),
                    log.diastolic.toString()
                )
            }, {
                it.printStackTrace()
            })
    }

    private fun setData(
        dateTime: MutableDateTime = MutableDateTime(),
        systolic: String = "",
        diastolic: String = ""
    ) {
        this.dateTime.value = state.get("date_time") ?: dateTime
        this.systolic.value = state.get("systolic") ?: systolic
        this.diastolic.value = state.get("diastolic") ?: diastolic
    }

    fun save() {
        when {
            systolic.getOrDefault("").isBlank() -> errorSystolicEmpty.value = true
            diastolic.getOrDefault("").isBlank() -> errorDiastolicEmpty.value = true
            else -> {
                Completable.fromAction {
                    dao.upsert(createLog())
                }.subscribeOn(io()).subscribe({
                    actionFinish.postValue(Event())
                }, {
                    it.printStackTrace()
                    showMessage.postValue(Event(R.string.error_saving))
                })
            }
        }
    }

    private fun createLog() = ApLog(
        systolic.getOrDefault("0").toInt(),
        diastolic.getOrDefault("0").toInt(),
        dateTime.getOrDateTime()
    ).also {
        it.id = id
    }

    fun delete() {
        if (id != 0L) dao.deleteById(id)
            .subscribeOn(io())
            .subscribe(
                { actionFinish.postValue(Event()) },
                {
                    it.printStackTrace()
                    showMessage.postValue(Event(R.string.error_deleting))
                }
            )
    }
}