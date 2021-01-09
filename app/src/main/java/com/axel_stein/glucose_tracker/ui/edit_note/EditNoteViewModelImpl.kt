package com.axel_stein.glucose_tracker.ui.edit_note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.model.NoteLog
import com.axel_stein.glucose_tracker.data.room.dao.NoteLogDao
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import org.joda.time.MutableDateTime

open class EditNoteViewModelImpl(private var id: Long = 0L) : ViewModel() {
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

    fun dateTimeLiveData(): LiveData<MutableDateTime> = dateTime

    fun noteLiveData(): LiveData<String> = note

    fun errorNoteEmptyLiveData(): LiveData<Boolean> = errorNoteEmpty

    fun errorSaveLiveData(): LiveData<Boolean> = errorSave

    fun errorDeleteLiveData(): LiveData<Boolean> = errorDelete

    fun actionFinishLiveData(): LiveData<Boolean> = actionFinish

    fun getCurrentDateTime(): DateTime = dateTime.value?.toDateTime() ?: DateTime()

    fun getNote() = note.value ?: ""

    fun loadData() {
        if (id != 0L) {
            dao.get(id).subscribeOn(Schedulers.io()).subscribe(object : SingleObserver<NoteLog> {
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
        val note = getNote()
        if (note.isEmpty()) {
            errorNoteEmpty.value = true
        } else {
            val log = createLog()
            var completable = dao.insert(log)
            if (log.id != 0L) {
                completable = dao.update(log)
            }
            completable.subscribeOn(Schedulers.io()).subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}

                override fun onComplete() {
                    actionFinish.postValue(true)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    errorSave.postValue(true)
                }
            })
        }
    }

    private fun createLog(): NoteLog {
        val note = getNote()
        return NoteLog(note, getCurrentDateTime()).also { it.id = id }
    }

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

    fun setNote(value: String) {
        note.value = value
        if (value.isNotEmpty()) {
            errorNoteEmpty.value = false
        }
    }

    fun delete() {
        if (id != 0L) {
            dao.deleteById(id).subscribeOn(Schedulers.io()).subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}

                override fun onComplete() {
                    actionFinish.postValue(true)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    errorDelete.postValue(true)
                }
            })
        }
    }
}