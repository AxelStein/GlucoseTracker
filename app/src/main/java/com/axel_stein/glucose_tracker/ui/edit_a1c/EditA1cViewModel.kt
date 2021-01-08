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

class EditA1cViewModel(
    private var id: Long = 0L,
    load: Boolean = true,
    _a1c: String = "",
    _dateTime: String? = null,
    dao: A1cLogDao? = null
) : ViewModel() {
    private val dateTime = MutableLiveData<MutableDateTime>()
    private val a1c = MutableLiveData<String>()
    private val errorValueEmpty = MutableLiveData<Boolean>()
    private val errorSave = MutableLiveData<Boolean>()
    private val errorDelete = MutableLiveData<Boolean>()
    private val actionFinish = MutableLiveData<Boolean>()

    @Inject
    lateinit var dao: A1cLogDao

    init {
        if (dao == null) {
            App.appComponent.inject(this)
        } else {
            this.dao = dao
        }

        if (load) {
            loadData()
        } else {
            this.a1c.value = _a1c
            this.dateTime.value = MutableDateTime(_dateTime)
        }
    }

    fun dateTimeLiveData(): LiveData<MutableDateTime> = dateTime

    fun valueLiveData(): LiveData<String> = a1c

    fun errorValueEmptyLiveData(): LiveData<Boolean> = errorValueEmpty

    fun errorSaveLiveData(): LiveData<Boolean> = errorSave

    fun errorDeleteLiveData(): LiveData<Boolean> = errorDelete

    fun actionFinishLiveData(): LiveData<Boolean> = actionFinish

    fun getId(): Long = id

    fun getCurrentDateTime(): DateTime = dateTime.value?.toDateTime() ?: DateTime()

    fun getValue() = a1c.value ?: ""

    private fun loadData() {
        if (id != 0L) {
            dao.get(id).subscribeOn(Schedulers.io()).subscribe(object : SingleObserver<A1cLog> {
                override fun onSubscribe(d: Disposable) {}

                override fun onSuccess(l: A1cLog) {
                    a1c.postValue(l.value.toString())
                    dateTime.postValue(l.dateTime.toMutableDateTime())
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
        } else {
            dateTime.postValue(MutableDateTime())
            a1c.postValue("")
        }
    }

    fun save() {
        if (getValue().isEmpty()) {
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
        a1c.value = value
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