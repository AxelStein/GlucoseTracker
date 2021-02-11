package com.axel_stein.glucose_tracker.ui.edit.edit_note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.google_drive.DriveWorkerScheduler
import com.axel_stein.glucose_tracker.data.room.dao.NoteLogDao
import com.axel_stein.glucose_tracker.data.room.model.NoteLog
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

class EditNoteViewModel(private val id: Long = 0L, private val state: SavedStateHandle) : ViewModel(), DateTimeProvider {
    private var dateTime = MutableLiveData<MutableDateTime>()

    private val note = MutableLiveData<String>()
    val noteLiveData: LiveData<String> = note

    private val errorNoteEmpty = MutableLiveData<Boolean>()
    val errorNoteEmptyLiveData: LiveData<Boolean> = errorNoteEmpty

    private val showMessage = MutableLiveData<Event<Int>>()
    val showMessageLiveData: LiveData<Event<Int>> = showMessage

    private val actionFinish = MutableLiveData<Event<Boolean>>()
    val actionFinishLiveData: LiveData<Event<Boolean>> = actionFinish

    private lateinit var dao: NoteLogDao
    private lateinit var scheduler: DriveWorkerScheduler

    init {
        App.appComponent.inject(this)
        loadData()
    }

    @Inject
    fun inject(dao: NoteLogDao, scheduler: DriveWorkerScheduler) {
        this.dao = dao
        this.scheduler = scheduler
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

    fun setNote(value: String) {
        note.value = value
        state["note"] = value
        if (value.isNotBlank()) {
            errorNoteEmpty.value = false
        }
    }

    fun loadData() {
        if (id == 0L) setData()
        else dao.get(id)
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe({ log ->
                setData(log.dateTime.toMutableDateTime(), log.note)
            }, {
                it.printStackTrace()
            })
    }

    private fun setData(dateTime: MutableDateTime = MutableDateTime(), note: String = "") {
        this.dateTime.value = state.get("date_time") ?: dateTime
        this.note.value = state.get("note") ?: note
    }

    fun save() {
        when {
            note.getOrDefault("").isBlank() -> errorNoteEmpty.value = true
            else -> {
                Completable.fromAction {
                    dao.upsert(createLog())
                }.subscribeOn(io()).subscribe({
                    scheduler.schedule()
                    actionFinish.postValue(Event())
                }, {
                    it.printStackTrace()
                    showMessage.postValue(Event(R.string.error_saving_note))
                })
            }
        }
    }

    private fun createLog() = NoteLog(
        note.getOrDefault(""),
        dateTime.getOrDateTime()
    ).also {
        it.id = id
    }

    fun delete() {
        if (id != 0L) dao.deleteById(id)
            .subscribeOn(io())
            .subscribe(
                {
                    scheduler.schedule()
                    actionFinish.postValue(Event())
                },
                {
                    it.printStackTrace()
                    showMessage.postValue(Event(R.string.error_deleting_note))
                }
            )
    }
}