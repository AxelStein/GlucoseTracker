package com.example.glucose_tracker.ui.edit_glucose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.glucose_tracker.data.model.GlucoseLog
import com.example.glucose_tracker.data.room.dao.GlucoseLogDao
import com.example.glucose_tracker.ui.App
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import javax.inject.Inject

class EditGlucoseViewModel : ViewModel() {
    private val _dateTime = MutableLiveData(MutableDateTime())
    val dateTime = _dateTime as LiveData<MutableDateTime>

    private val _glucose = MutableLiveData(0f)
    val glucose = _glucose as LiveData<Float>

    private val _measured = MutableLiveData(0)
    val measured = _measured as LiveData<Int>

    private val _errorGlucoseEmpty = MutableLiveData(false)
    val errorGlucoseEmpty = _errorGlucoseEmpty as LiveData<Boolean>

    private val _actionFinish = MutableLiveData(false)
    val actionFinish = _actionFinish as LiveData<Boolean>

    @Inject
    lateinit var dao: GlucoseLogDao

    init {
        App.appComponent.inject(this)
    }

    fun save() {
        if (_glucose.value != null && _glucose.value == 0f) {
            _errorGlucoseEmpty.value = true
        } else {
            val log = GlucoseLog(
                    null,
                    _glucose.value ?: 0f,
                    intoMgDl(_glucose.value),
                    _measured.value ?: 0,
                    _dateTime.value?.toDateTime() ?: DateTime(),
            )
            dao.insert(log).subscribeOn(io()).subscribe()
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
}