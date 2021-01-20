package com.axel_stein.glucose_tracker.ui.edit_pulse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.model.PulseLog
import com.axel_stein.glucose_tracker.data.room.dao.PulseLogDao
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.utils.DateTimeProvider
import com.axel_stein.glucose_tracker.utils.getOrDateTime
import com.axel_stein.glucose_tracker.utils.getOrDefault
import com.axel_stein.glucose_tracker.utils.ui.Event
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.MutableDateTime
import javax.inject.Inject

class EditPulseViewModel(private val id: Long = 0L, private val state: SavedStateHandle) : ViewModel(),
    DateTimeProvider {
    private var dateTime = MutableLiveData<MutableDateTime>()

    private val pulse = MutableLiveData<String>()
    val pulseLiveData: LiveData<String> = pulse

    private val errorPulseEmpty = MutableLiveData<Boolean>()
    val errorPulseEmptyLiveData: LiveData<Boolean> = errorPulseEmpty

    private val showMessage = MutableLiveData<Event<Int>>()
    val showMessageLiveData: LiveData<Event<Int>> = showMessage

    private val actionFinish = MutableLiveData<Event<Boolean>>()
    val actionFinishLiveData: LiveData<Event<Boolean>> = actionFinish

    private lateinit var dao: PulseLogDao

    init {
        App.appComponent.inject(this)
        loadData()
    }

    @Inject
    fun setDao(dao: PulseLogDao) {
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

    fun setPulse(value: String) {
        this.pulse.value = value
        state["pulse"] = value
        if (value.isNotBlank()) {
            errorPulseEmpty.value = false
        }
    }

    fun loadData() {
        if (id == 0L) setData()
        else dao.getById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ log ->
                setData(
                    log.dateTime.toMutableDateTime(),
                    log.value.toString(),
                )
            }, {
                it.printStackTrace()
            })
    }

    private fun setData(
        dateTime: MutableDateTime = MutableDateTime(),
        systolic: String = ""
    ) {
        this.dateTime.value = state.get("date_time") ?: dateTime
        this.pulse.value = state.get("systolic") ?: systolic
    }

    fun save() {
        when {
            pulse.getOrDefault("").isBlank() -> errorPulseEmpty.value = true
            else -> {
                Completable.fromAction {
                    dao.upsert(createLog())
                }.subscribeOn(Schedulers.io()).subscribe({
                    actionFinish.postValue(Event())
                }, {
                    it.printStackTrace()
                    showMessage.postValue(Event(R.string.error_saving))
                })
            }
        }
    }

    private fun createLog() = PulseLog(
        pulse.getOrDefault("0").toInt(),
        dateTime.getOrDateTime()
    ).also {
        it.id = id
    }

    fun delete() {
        if (id != 0L) dao.deleteById(id)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { actionFinish.postValue(Event()) },
                {
                    it.printStackTrace()
                    showMessage.postValue(Event(R.string.error_deleting))
                }
            )
    }
}