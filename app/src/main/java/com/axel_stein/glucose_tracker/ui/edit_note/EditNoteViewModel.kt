package com.axel_stein.glucose_tracker.ui.edit_note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.model.NoteLog
import com.axel_stein.glucose_tracker.data.room.dao.NoteLogDao
import com.axel_stein.glucose_tracker.ui.App
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import javax.inject.Inject

class EditNoteViewModel : ViewModel() {
    private val dateTime = MutableLiveData<MutableDateTime>()
    private val noteData = MutableLiveData<String>()
    private val errorNoteEmpty = MutableLiveData<Boolean>()
    private val errorSave = MutableLiveData<Boolean>()
    private val errorDelete = MutableLiveData<Boolean>()
    private val actionFinish = MutableLiveData<Boolean>()
    private var id = 0L
    private var loadData = true

    @Inject
    lateinit var dao: NoteLogDao

    init {
        App.appComponent.inject(this)
    }

    fun dateTimeObserver(): LiveData<MutableDateTime> {
        return dateTime
    }

    fun noteObserver(): LiveData<String> {
        return noteData
    }

    fun errorNoteEmptyObserver(): LiveData<Boolean> {
        return errorNoteEmpty
    }

    fun errorSaveObserver(): LiveData<Boolean> {
        return errorSave
    }

    fun errorDeleteObserver(): LiveData<Boolean> {
        return errorDelete
    }

    fun actionFinishObserver(): LiveData<Boolean> {
        return actionFinish
    }

    fun getId(): Long = id

    fun getCurrentDateTime(): DateTime = dateTime.value?.toDateTime() ?: DateTime()

    fun getNote() = noteData.value ?: ""

    fun shouldRestore() = loadData

    fun restore(id: Long, dateTime: String?, note: String?) {
        this.id = id
        this.dateTime.value = MutableDateTime(dateTime)
        this.noteData.value = note
        loadData = false
    }

    fun loadData(id: Long) {
        if (loadData) {
            this.id = id
            this.loadData = false

            if (id != 0L) {
                dao.get(id).subscribeOn(Schedulers.io()).subscribe(object : SingleObserver<NoteLog> {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(l: NoteLog) {
                        dateTime.postValue(l.dateTime.toMutableDateTime())
                        noteData.postValue(l.note)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
            } else {
                dateTime.postValue(MutableDateTime())
                noteData.postValue("")
            }
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
        noteData.value = value
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