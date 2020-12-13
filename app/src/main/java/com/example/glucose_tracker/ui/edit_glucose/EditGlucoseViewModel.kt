package com.example.glucose_tracker.ui.edit_glucose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.glucose_tracker.data.model.GlucoseLog
import com.example.glucose_tracker.data.room.GlucoseLogDao
import com.example.glucose_tracker.ui.App
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import javax.inject.Inject

class EditGlucoseViewModel : ViewModel() {
    private val _date = MutableLiveData(LocalDate())
    val date = _date as LiveData<LocalDate>

    private val _time = MutableLiveData(LocalTime())
    val time = _time as LiveData<LocalTime>

    private val _glucose = MutableLiveData(0f)
    val glucose = _glucose as LiveData<Float>

    private val _measured = MutableLiveData(0)
    val measured = _measured as LiveData<Int>

    @Inject
    lateinit var dao: GlucoseLogDao

    init {
        App.appComponent.inject(this)
    }

    fun save() {
        val log = GlucoseLog(null,
                _glucose.value ?: 0f,
                intoMgDl(_glucose.value),
                _measured.value ?: 0,
                _date.value ?: LocalDate(),
                _time.value ?: LocalTime()
        )
        dao.insert(log).subscribeOn(io()).subscribe()
    }

    private fun intoMgDl(mmolL: Float?): Int {
        return ((mmolL ?: 0f) * 18f).toInt()
    }

    fun setDate(date: LocalDate) {
        _date.postValue(date)
    }

    fun setTime(time: LocalTime) {
        _time.postValue(time)
    }

    fun setGlucose(value: String) {
        _glucose.postValue(if (value.isEmpty()) 0f else value.toFloat())
    }

    fun setMeasured(measured: Int) {
        _measured.postValue(measured)
    }
}