package com.axel_stein.glucose_tracker.ui.edit_note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.model.NoteLog
import com.axel_stein.glucose_tracker.data.room.dao.NoteLogDao
import com.axel_stein.glucose_tracker.ui.App
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import javax.inject.Inject

class EditNoteViewModel(private var id: Long = 0L, state: SavedStateHandle) : ViewModel() {
    private val dateTime : MutableLiveData<MutableDateTime> = state.getLiveData("date_time")
    private val note : MutableLiveData<String> = state.getLiveData("note")
    private val errorNoteEmpty : MutableLiveData<Boolean> = state.getLiveData("error_note")
    private val errorSave : MutableLiveData<Boolean> = state.getLiveData("error_save")
    private val errorDelete : MutableLiveData<Boolean> = state.getLiveData("error_delete")
    private val actionFinish : MutableLiveData<Boolean> = state.getLiveData("action_finish")

    @Inject
    lateinit var dao: NoteLogDao

    init {
        App.appComponent.inject(this)
        if (!state.contains("id")) {
            state["id"] = id
            loadData()
        }
    }

    fun dateTimeLiveData(): LiveData<MutableDateTime> = dateTime

    fun noteLiveData(): LiveData<String> = note

    fun errorNoteEmptyLiveData(): LiveData<Boolean> = errorNoteEmpty

    fun errorSaveLiveData(): LiveData<Boolean> = errorSave

    fun errorDeleteLiveData(): LiveData<Boolean> = errorDelete

    fun actionFinishLiveData(): LiveData<Boolean> = actionFinish

    fun getCurrentDateTime(): DateTime = dateTime.value?.toDateTime() ?: DateTime()

    fun getNote() = note.value ?: ""

    private fun loadData() {
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
        val note = getNote()
        if (note.isEmpty()) {
            errorNoteEmpty.value = true
        } else {
            val log = createLog()
            var completable = dao.insert(log)
            if (log.id != 0L) {
                completable = dao.update(log)
            }
            completable.subscribeOn(io()).subscribe(object : CompletableObserver {
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
            dao.deleteById(id).subscribeOn(io()).subscribe(object : CompletableObserver {
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