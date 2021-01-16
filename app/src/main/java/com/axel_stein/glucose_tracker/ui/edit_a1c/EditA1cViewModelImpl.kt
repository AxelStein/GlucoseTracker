package com.axel_stein.glucose_tracker.ui.edit_a1c

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.model.A1cLog
import com.axel_stein.glucose_tracker.data.room.dao.A1cLogDao
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.DateTime
import org.joda.time.MutableDateTime

open class EditA1cViewModelImpl(private val id: Long = 0L) : ViewModel() {
    protected var dateTime = MutableLiveData<MutableDateTime>()
    protected var a1c = MutableLiveData<String>()
    protected var errorValueEmpty = MutableLiveData<Boolean>()
    protected var errorSave = MutableLiveData<Boolean>()
    protected var errorDelete = MutableLiveData<Boolean>()
    protected var actionFinish = MutableLiveData<Boolean>()
    private lateinit var dao: A1cLogDao

    fun setDao(dao: A1cLogDao) {
        this.dao = dao
    }

    fun dateTimeLiveData(): LiveData<MutableDateTime> = dateTime

    fun valueLiveData(): LiveData<String> = a1c

    fun errorValueEmptyLiveData(): LiveData<Boolean> = errorValueEmpty

    fun errorSaveLiveData(): LiveData<Boolean> = errorSave

    fun errorDeleteLiveData(): LiveData<Boolean> = errorDelete

    fun actionFinishLiveData(): LiveData<Boolean> = actionFinish

    fun getCurrentDateTime(): DateTime = dateTime.value?.toDateTime() ?: DateTime()

    fun getValue() = a1c.value ?: ""

    fun loadData() {
        if (id == 0L) postData()
        else Single.fromCallable { dao.getById(id) }
            .subscribeOn(io())
            .subscribe({
                a1c.postValue(it.value.toString())
                dateTime.postValue(it.dateTime.toMutableDateTime())
            }, {

            })
    }

    private fun postData(a1cValue: String = "", dateTime: MutableDateTime = MutableDateTime()) {
        a1c.postValue(a1cValue)
        this.dateTime.postValue(dateTime)
    }

    @SuppressLint("CheckResult")
    fun save() {
        if (getValue().isEmpty()) {
            errorValueEmpty.value = true
        } else {
            Completable.fromAction {
                dao.upsert(createLog())
            }.subscribeOn(io()).subscribe({
                actionFinish.postValue(true)
            }, {
                it.printStackTrace()
                errorSave.postValue(true)
            })
        }
    }

    private fun createLog(): A1cLog = A1cLog(
        getValue().toFloat(), getCurrentDateTime()
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
        if (id != 0L) Single.fromCallable { dao.deleteById(id) }
            .subscribeOn(io())
            .subscribe({
                actionFinish.postValue(true)
            }, {
                it.printStackTrace()
                errorDelete.postValue(true)
            })
    }
}