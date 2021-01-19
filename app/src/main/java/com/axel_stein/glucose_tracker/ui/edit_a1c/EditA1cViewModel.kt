package com.axel_stein.glucose_tracker.ui.edit_a1c

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.model.A1cLog
import com.axel_stein.glucose_tracker.data.room.dao.A1cLogDao
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.utils.getOrDateTime
import com.axel_stein.glucose_tracker.utils.getOrDefault
import com.axel_stein.glucose_tracker.utils.ui.Event
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import javax.inject.Inject

class EditA1cViewModel(private val id: Long = 0L, state: SavedStateHandle) : ViewModel() {
    private val dateTime = state.getLiveData<MutableDateTime>("date_time")
    val dateTimeLiveData: LiveData<MutableDateTime> = dateTime

    private val a1c = state.getLiveData<String>("a1c")
    val a1cLiveData: LiveData<String> = a1c

    private val errorValueEmpty = MutableLiveData<Boolean>()
    val errorValueEmptyLiveData: LiveData<Boolean> = errorValueEmpty

    private val actionFinish = MutableLiveData<Event<Boolean>>()
    val actionFinishLiveData: LiveData<Event<Boolean>> = actionFinish

    private val showMessage = MutableLiveData<Event<Int>>()
    val showMessageLiveData: LiveData<Event<Int>> = showMessage

    private lateinit var dao: A1cLogDao

    init {
        App.appComponent.inject(this)
        if (!state.contains("id")) {
            state["id"] = id
            loadData()
        }
    }

    @Inject
    fun setDao(dao: A1cLogDao) {
        this.dao = dao
    }

    fun getCurrentDateTime(): DateTime = dateTime.getOrDateTime()

    fun loadData() {
        if (id == 0L) postData()
        else Single.fromCallable { this.dao.getById(id) }
            .subscribeOn(io())
            .subscribe({
                a1c.postValue(it.value.toString())
                dateTime.postValue(it.dateTime.toMutableDateTime())
            }, {
                it.printStackTrace()
            })
    }

    private fun postData(a1cValue: String = "", dateTime: MutableDateTime = MutableDateTime()) {
        a1c.postValue(a1cValue)
        this.dateTime.postValue(dateTime)
    }

    @SuppressLint("CheckResult")
    fun save() {
        if (a1c.getOrDefault("").isEmpty()) {
            errorValueEmpty.value = true
        } else {
            Completable.fromAction {
                this.dao.upsert(createLog())
            }.subscribeOn(io()).subscribe({
                actionFinish.postValue(Event())
            }, {
                it.printStackTrace()
                showMessage.postValue(Event(R.string.error_saving_log))
            })
        }
    }

    private fun createLog(): A1cLog = A1cLog(
        a1c.getOrDefault("0").toFloat(),
        getCurrentDateTime()
    ).also { it.id = id }

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

    fun setValue(value: String) {
        a1c.value = value
        if (value.isNotEmpty()) {
            errorValueEmpty.value = false
        }
    }

    fun delete() {
        if (id != 0L) Single.fromCallable { this.dao.deleteById(id) }
            .subscribeOn(io())
            .subscribe({
                actionFinish.postValue(Event())
            }, {
                it.printStackTrace()
                showMessage.postValue(Event(R.string.error_deleting_log))
            })
    }
}