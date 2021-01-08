package com.axel_stein.glucose_tracker.ui.edit_a1c

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.model.A1cLog
import com.axel_stein.glucose_tracker.data.room.dao.A1cLogDao
import com.axel_stein.glucose_tracker.ui.App
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import javax.inject.Inject

class EditA1cViewModel(private val id: Long = 0L, state: SavedStateHandle) : ViewModel() {
    private val dateTime : MutableLiveData<MutableDateTime> = state.getLiveData("date_time")
    private val a1c : MutableLiveData<String> = state.getLiveData("a1c")
    private val errorValueEmpty : MutableLiveData<Boolean> = state.getLiveData("error_value_empty")
    private val errorSave : MutableLiveData<Boolean> = state.getLiveData("error_save")
    private val errorDelete : MutableLiveData<Boolean> = state.getLiveData("error_delete")
    private val actionFinish : MutableLiveData<Boolean> = state.getLiveData("action_finish")

    @Inject
    lateinit var dao: A1cLogDao

    init {
        App.appComponent.inject(this)
        if (!state.contains("id")) {
            state["id"] = id
            loadData()
        }
    }

    fun dateTimeLiveData(): LiveData<MutableDateTime> = dateTime

    fun valueLiveData(): LiveData<String> = a1c

    fun errorValueEmptyLiveData(): LiveData<Boolean> = errorValueEmpty

    fun errorSaveLiveData(): LiveData<Boolean> = errorSave

    fun errorDeleteLiveData(): LiveData<Boolean> = errorDelete

    fun actionFinishLiveData(): LiveData<Boolean> = actionFinish

    fun getCurrentDateTime(): DateTime = dateTime.value?.toDateTime() ?: DateTime()

    fun getValue() = a1c.value ?: ""

    private fun loadData() {
        if (id != 0L) {
            dao.get(id).subscribeOn(io()).subscribe(object : SingleObserver<A1cLog> {
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