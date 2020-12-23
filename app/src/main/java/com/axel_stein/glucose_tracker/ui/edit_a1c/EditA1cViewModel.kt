package com.axel_stein.glucose_tracker.ui.edit_a1c

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.model.A1cLog
import com.axel_stein.glucose_tracker.data.room.dao.A1cLogDao
import com.axel_stein.glucose_tracker.ui.App
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import javax.inject.Inject

class EditA1cViewModel: ViewModel() {
    private val dateTime = MutableLiveData<MutableDateTime>()
    private val data = MutableLiveData<String>()
    private val errorValueEmpty = MutableLiveData<Boolean>()
    private val errorSave = MutableLiveData<Boolean>()
    private val errorDelete = MutableLiveData<Boolean>()
    private val actionFinish = MutableLiveData<Boolean>()
    private var id = 0L
    private var loadData = true

    @Inject
    lateinit var dao: A1cLogDao

    init {
        App.appComponent.inject(this)
    }

    fun dateTimeObserver(): LiveData<MutableDateTime> {
        return dateTime
    }

    fun valueObserver(): LiveData<String> {
        return data
    }

    fun errorValueEmptyObserver(): LiveData<Boolean> {
        return errorValueEmpty
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

    fun getValue() = data.value ?: ""

    fun shouldRestore() = loadData

    fun restore(id: Long, dateTime: String?, note: String?) {
        this.id = id
        this.dateTime.value = MutableDateTime(dateTime)
        this.data.value = note
        loadData = false
    }

    fun loadData(id: Long) {
        if (loadData) {
            this.id = id
            this.loadData = false

            if (id != 0L) {
                dao.get(id).subscribeOn(Schedulers.io()).subscribe(object : SingleObserver<A1cLog> {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(l: A1cLog) {
                        data.postValue(l.value.toString())
                        dateTime.postValue(l.dateTime.toMutableDateTime())
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
            } else {
                dateTime.postValue(MutableDateTime())
                data.postValue("")
            }
        }
    }

    fun save() {
        val note = getValue()
        if (note.isEmpty()) {
            errorValueEmpty.value = true
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

    private fun createLog(): A1cLog {
        return A1cLog(getValue().toFloat(), getCurrentDateTime()).also { it.id = id }
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

    fun setValue(value: String) {
        data.value = value
        if (value.isNotEmpty()) {
            errorValueEmpty.value = false
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