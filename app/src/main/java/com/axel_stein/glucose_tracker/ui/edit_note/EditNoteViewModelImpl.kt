package com.axel_stein.glucose_tracker.ui.edit_note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.model.NoteLog
import com.axel_stein.glucose_tracker.data.room.dao.NoteLogDao
import com.axel_stein.glucose_tracker.utils.DateTimeProvider
import com.axel_stein.glucose_tracker.utils.getOrDateTime
import com.axel_stein.glucose_tracker.utils.getOrDefault
import io.reactivex.Completable
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.MutableDateTime

open class EditNoteViewModelImpl(private var id: Long = 0L) : ViewModel(), DateTimeProvider {
    protected var dateTime = MutableLiveData<MutableDateTime>()
    protected var note = MutableLiveData<String>()
    protected var errorNoteEmpty = MutableLiveData<Boolean>()
    protected var errorSave = MutableLiveData<Boolean>()
    protected var errorDelete = MutableLiveData<Boolean>()
    protected var actionFinish = MutableLiveData<Boolean>()
    private lateinit var dao: NoteLogDao

    fun setDao(dao: NoteLogDao) {
        this.dao = dao
    }

    fun noteLiveData(): LiveData<String> = note

    fun errorNoteEmptyLiveData(): LiveData<Boolean> = errorNoteEmpty

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
        if (id != 0L) {
            dao.get(id).subscribeOn(io()).subscribe(object : SingleObserver<NoteLog> {
                override fun onSubscribe(d: Disposable) {}

                override fun onSuccess(l: NoteLog) {
                    dateTime.postValue(l.dateTime.toMutableDateTime())
                    note.postValue(l.note)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
        } else {
            dateTime.postValue(MutableDateTime())
            note.postValue("")
        }
    }

    fun save() {
        when {
            note.getOrDefault("").isBlank() -> errorNoteEmpty.value = true
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

    private fun createLog() = NoteLog(
        note.getOrDefault(""),
        dateTime.getOrDateTime()
    ).also {
        it.id = id
    }

    fun setNote(value: String) {
        note.value = value
        if (value.isNotBlank()) {
            errorNoteEmpty.value = false
        }
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