package com.example.glucose_tracker.ui.edit_glucose

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.glucose_tracker.data.model.GlucoseLog
import com.example.glucose_tracker.data.room.dao.GlucoseLogDao
import com.example.glucose_tracker.ui.App
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import javax.inject.Inject

class EditGlucoseViewModel : ViewModel() {
    private val _dateTime = MutableLiveData(MutableDateTime())
    val dateTime = _dateTime as LiveData<MutableDateTime>

    private val _glucose = MutableLiveData<Float>()
    val glucose = _glucose as LiveData<Float>

    private val _measured = MutableLiveData<Int>()
    val measured = _measured as LiveData<Int>

    private val _errorGlucoseEmpty = MutableLiveData(false)
    val errorGlucoseEmpty = _errorGlucoseEmpty as LiveData<Boolean>

    private val _actionFinish = MutableLiveData(false)
    val actionFinish = _actionFinish as LiveData<Boolean>

    private var log: GlucoseLog? = null

    @Inject
    lateinit var dao: GlucoseLogDao

    init {
        App.appComponent.inject(this)
    }

    fun loadData(id: Long) {
        if (id != 0L && log == null) {
            dao.get(id).subscribeOn(io()).subscribe(object : SingleObserver<GlucoseLog> {
                override fun onSubscribe(d: Disposable) {}

                override fun onSuccess(l: GlucoseLog) {
                    Log.d("TAG", "onSuccess $l")
                    log = l
                    _dateTime.postValue(l.dateTime.toMutableDateTime())
                    _glucose.postValue(l.valueMmol)
                    _measured.postValue(l.measured)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
        } else {
            _dateTime.postValue(MutableDateTime())
            _glucose.postValue(0f)
            _measured.postValue(0)
        }
    }

    fun save() {
        if (_glucose.value != null && _glucose.value == 0f) {
            _errorGlucoseEmpty.value = true
        } else {
            val log = GlucoseLog(
                    log?.id,
                    _glucose.value ?: 0f,
                    intoMgDl(_glucose.value),
                    _measured.value ?: 0,
                    _dateTime.value?.toDateTime() ?: DateTime(),
            )
            var completable = dao.insert(log)
            if (log.id != null) {
                completable = dao.update(log)
            }
            completable.subscribeOn(io()).subscribe()
            _actionFinish.value = true
        }
    }

    private fun intoMgDl(mmolL: Float?): Int {
        return ((mmolL ?: 0f) * 18f).toInt()
    }

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        _dateTime.value.apply {
            this?.year = year
            this?.monthOfYear = month
            this?.dayOfMonth = dayOfMonth
        }
        _dateTime.value = _dateTime.value
    }

    fun setTime(hourOfDay: Int, minuteOfHour: Int) {
        _dateTime.value.apply {
            this?.hourOfDay = hourOfDay
            this?.minuteOfHour = minuteOfHour
        }
        _dateTime.value = _dateTime.value
    }

    fun setGlucose(value: String) {
        val g = if (value.isEmpty()) 0f else value.toFloat()
        if (g > 0) {
            _errorGlucoseEmpty.value = false
        }
        _glucose.value = g
    }

    fun setMeasured(measured: Int) {
        _measured.value = measured
    }

    fun delete() {
        log?.let {
            dao.delete(it).subscribeOn(io()).subscribe()
            _actionFinish.value = true
        }
    }
}