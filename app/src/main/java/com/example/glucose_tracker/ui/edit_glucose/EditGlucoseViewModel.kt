package com.example.glucose_tracker.ui.edit_glucose

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.glucose_tracker.data.model.GlucoseLog
import com.example.glucose_tracker.data.room.dao.GlucoseLogDao
import com.example.glucose_tracker.ui.App
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import javax.inject.Inject

class EditGlucoseViewModel : ViewModel() {
    private val dateTime = MutableLiveData<MutableDateTime>()
    private val glucose = MutableLiveData<String>()
    private val measured = MutableLiveData<Int>()
    private val errorGlucoseEmpty = MutableLiveData<Boolean>()
    private val actionFinish = MutableLiveData<Boolean>()
    private var id = 0L
    private var loadData = true

    @Inject
    lateinit var dao: GlucoseLogDao

    init {
        App.appComponent.inject(this)
    }

    fun dateTimeObserver(): LiveData<MutableDateTime> {
        return dateTime
    }

    fun glucoseObserver(): LiveData<String> {
        return glucose
    }

    fun measuredObserver(): LiveData<Int> {
        return measured
    }

    fun errorGlucoseEmptyObserver(): LiveData<Boolean> {
        return errorGlucoseEmpty
    }

    fun actionFinishObserver(): LiveData<Boolean> {
        return actionFinish
    }

    fun getCurrentDateTime(): DateTime {
        return dateTime.value?.toDateTime() ?: DateTime()
    }

    fun loadData(id: Long) {
        if (loadData) {
            this.id = id
            this.loadData = false

            if (id != 0L) {
                dao.get(id).subscribeOn(io()).subscribe(object : SingleObserver<GlucoseLog> {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(l: GlucoseLog) {
                        dateTime.postValue(l.dateTime.toMutableDateTime())
                        glucose.postValue(l.valueMmol.toString())
                        measured.postValue(l.measured)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
            } else {
                dateTime.postValue(MutableDateTime())
                glucose.postValue("")
                measured.postValue(0)
            }
        }
    }

    fun save() {
        val g = glucose.value ?: ""
        if (g.isEmpty()) {
            errorGlucoseEmpty.value = true
        } else {
            val log = createLog()
            var completable = dao.insert(log)
            if (log.id != 0L) {
                completable = dao.update(log)
            }
            Log.d("TAG", "save $log")
            completable.subscribeOn(io()).subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onComplete() {
                    Log.d("TAG", "onComplete")
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
            actionFinish.value = true
        }
    }

    private fun createLog(): GlucoseLog {
        val g = glucose.value?.toFloat() ?: 0f
        return GlucoseLog(
            if (id == 0L) null else id,
                glucose.value?.toFloat() ?: 0f,
            intoMgDl(g),
            measured.value ?: 0,
            dateTime.value?.toDateTime() ?: DateTime()
        )
    }

    private fun intoMgDl(mmolL: Float?): Int {
        return ((mmolL ?: 0f) * 18f).toInt()
    }

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        dateTime.value.apply {
            this?.year = year
            this?.monthOfYear = month
            this?.dayOfMonth = dayOfMonth
        }
        dateTime.postValue(dateTime.value)
    }

    fun setTime(hourOfDay: Int, minuteOfHour: Int) {
        dateTime.value.apply {
            this?.hourOfDay = hourOfDay
            this?.minuteOfHour = minuteOfHour
        }
        dateTime.postValue(dateTime.value)
    }

    fun setGlucose(value: String) {
        glucose.value = value
        if (value.isNotEmpty()) {
            errorGlucoseEmpty.value = false
        }
    }

    fun setMeasured(measured: Int) {
        this.measured.value = measured
    }

    fun delete() {
        if (id != 0L) {
            dao.deleteById(id).subscribeOn(io()).subscribe()
            actionFinish.value = true
        }
    }
}