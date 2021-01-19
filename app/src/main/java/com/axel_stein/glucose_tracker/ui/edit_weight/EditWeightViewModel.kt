package com.axel_stein.glucose_tracker.ui.edit_weight

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.model.WeightLog
import com.axel_stein.glucose_tracker.data.room.dao.WeightLogDao
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.utils.*
import com.axel_stein.glucose_tracker.utils.ui.Event
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.MutableDateTime
import javax.inject.Inject
import kotlin.math.pow

class EditWeightViewModel(private val id: Long = 0L, private val state: SavedStateHandle) : ViewModel(), DateTimeProvider {
    private val dateTime = MutableLiveData<MutableDateTime>()

    private val weight = MutableLiveData<String>()
    val weightLiveData: LiveData<String> = weight

    private val bmiResult = MutableLiveData<BMI>()
    val bmiResultLiveData: LiveData<BMI> = bmiResult

    private val showHintIndicateHeight = MutableLiveData<Boolean>()
    val showHintIndicateHeightLiveData: LiveData<Boolean> = showHintIndicateHeight

    private val errorEmpty = MutableLiveData<Boolean>()
    val errorNoteEmptyLiveData: LiveData<Boolean> = errorEmpty

    private val actionFinish = MutableLiveData<Event<Boolean>>()
    val actionFinishLiveData: LiveData<Event<Boolean>> = actionFinish

    private val showMessage = MutableLiveData<Event<Int>>()
    val showMessageLiveData: LiveData<Event<Int>> = showMessage

    private lateinit var dao: WeightLogDao
    private lateinit var settings: AppSettings

    init {
        App.appComponent.inject(this)
        loadData()
    }

    @Inject
    fun setDao(dao: WeightLogDao) {
        this.dao = dao
    }

    @Inject
    fun setSettings(settings: AppSettings) {
        this.settings = settings
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

    fun loadData() {
        if (id == 0L) setData()
        else dao.getById(id)
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe({
                setData(
                    it.dateTime.toMutableDateTime(),
                    it.kg.formatIfInt()
                )
            }, {
                it.printStackTrace()
            })
    }

    private fun setData(dateTime: MutableDateTime = MutableDateTime(), kg: String = "") {
        this.dateTime.value = state.get("date_time") ?: dateTime
        weight.value = state.get("weight") ?: kg
    }

    fun setWeight(value: String) {
        this.weight.value = value
        state["weight"] = value
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
                    actionFinish.postValue(Event())
                }, {
                    it.printStackTrace()
                    showMessage.postValue(Event(R.string.error_saving_log))
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
                { actionFinish.postValue(Event()) },
                {
                    it.printStackTrace()
                    showMessage.postValue(Event(R.string.error_deleting_log))
                }
            )
    }

    data class BMI(
        val value: Float,
        val category: Int
    )
}