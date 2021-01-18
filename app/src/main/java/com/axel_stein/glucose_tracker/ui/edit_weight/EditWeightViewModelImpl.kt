package com.axel_stein.glucose_tracker.ui.edit_weight

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.model.WeightLog
import com.axel_stein.glucose_tracker.data.room.dao.WeightLogDao
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.utils.*
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.MutableDateTime
import kotlin.math.pow

open class EditWeightViewModelImpl(private val id: Long = 0L) : ViewModel(), DateTimeProvider {
    protected var dateTime = MutableLiveData<MutableDateTime>()
    protected var weight = MutableLiveData<String>()
    protected var bmiResult = MutableLiveData<BMI>()
    protected var showHintIndicateHeight = MutableLiveData<Boolean>()
    protected var errorEmpty = MutableLiveData<Boolean>()
    protected var errorSave = MutableLiveData<Boolean>()
    protected var errorDelete = MutableLiveData<Boolean>()
    protected var actionFinish = MutableLiveData<Boolean>()
    private lateinit var dao: WeightLogDao
    private lateinit var settings: AppSettings

    fun setDao(dao: WeightLogDao) {
        this.dao = dao
    }

    fun setAppSettings(appSettings: AppSettings) {
        this.settings = appSettings
    }

    fun weightLiveData(): LiveData<String> = weight

    fun bmiResultLiveData(): LiveData<BMI> = bmiResult

    fun showHintIndicateHeight(): LiveData<Boolean> = showHintIndicateHeight

    fun errorNoteEmptyLiveData(): LiveData<Boolean> = errorEmpty

    fun errorSaveLiveData(): LiveData<Boolean> = errorSave

    fun errorDeleteLiveData(): LiveData<Boolean> = errorDelete

    fun actionFinishLiveData(): LiveData<Boolean> = actionFinish

    override fun dateTimeLiveData(): LiveData<MutableDateTime> = dateTime

    override fun onDateSet(year: Int, month: Int, dayOfMonth: Int) {
        val dt = dateTime.getOrDefault()
        dateTime.postValue(
            dt.apply {
                this.year = year
                this.monthOfYear = month
                this.dayOfMonth = dayOfMonth
            }
        )
    }

    override fun onTimeSet(hourOfDay: Int, minuteOfHour: Int) {
        val dt = dateTime.getOrDefault()
        dateTime.postValue(
            dt.apply {
                this.hourOfDay = hourOfDay
                this.minuteOfHour= minuteOfHour
            }
        )
    }

    fun loadData() {
        if (id == 0L) postData()
        else dao.getById(id)
            .subscribeOn(io())
            .subscribe({
                postData(
                    it.dateTime.toMutableDateTime(),
                    it.kg.formatIfInt()
                )
            }, {
                it.printStackTrace()
            })
    }

    private fun postData(dateTime: MutableDateTime = MutableDateTime(), kg: String = "") {
        this.dateTime.postValue(dateTime)
        weight.postValue(kg)
    }

    fun setWeight(value: String) {
        this.weight.value = value
        if (value.isNotBlank()) {
            errorEmpty.value = false
            try {
                calculateBMI(value.toFloat())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            calculateBMI(0f)
        }
    }

    private fun calculateBMI(kg: Float) {
        var height = 0f
        try {
            height = settings.getHeight().toFloat()
        } catch (e: Exception) {
        }
        val h = height.intoMeter()
        if (h > 0f) {
            if (kg > 0f) {
                val value = kg.div(h.pow(2)).round()
                val bmi = BMI(
                    value,
                    when {
                        value < 16f -> 0
                        value < 17f -> 1
                        value < 18.5f -> 2
                        value < 25f -> 3
                        value < 30f -> 4
                        value < 35f -> 5
                        value < 40f -> 6
                        else -> 7
                    }
                )
                bmiResult.postValue(bmi)
            } else {
                bmiResult.postValue(BMI(0f, 0))
            }
        } else {
            showHintIndicateHeight.postValue(true)
        }
    }

    fun save() {
        when {
            weight.getOrDefault("").isBlank() -> errorEmpty.value = true
            else -> {
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
    }

    private fun createLog(): WeightLog {
        val w = weight.getOrDefault("0").toFloat().round()
        val inLbs = w.intoLb()
        return WeightLog(
            w, inLbs, dateTime.getOrDateTime()
        ).also { it.id = id }
    }

    fun delete() {
        if (id != 0L) dao.deleteById(id)
            .subscribeOn(io())
            .subscribe(
                { actionFinish.postValue(true) },
                {
                    it.printStackTrace()
                    errorDelete.postValue(true)
                }
            )
    }
}

data class BMI(
    val value: Float,
    val category: Int
)
