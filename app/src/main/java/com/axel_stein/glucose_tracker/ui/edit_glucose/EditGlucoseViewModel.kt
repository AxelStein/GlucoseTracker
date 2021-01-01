package com.axel_stein.glucose_tracker.ui.edit_glucose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.model.GlucoseLog
import com.axel_stein.glucose_tracker.data.room.dao.GlucoseLogDao
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.App
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import javax.inject.Inject
import kotlin.math.roundToInt

class EditGlucoseViewModel(
    private var id: Long = 0L,
    load: Boolean = true,
    glucose: String = "",
    measured: Int = 0,
    dateTime: String? = null,
    dao: GlucoseLogDao? = null,
    appSettings: AppSettings? = null,
    appResources: AppResources? = null
) : ViewModel() {
    private val dateTime = MutableLiveData<MutableDateTime>()
    private val glucose = MutableLiveData<String>()
    private val measured = MutableLiveData<Int>()
    private val errorLoading = MutableLiveData<Boolean>()
    private val errorGlucoseEmpty = MutableLiveData<Boolean>()
    private val errorSave = MutableLiveData<Boolean>()
    private val errorDelete = MutableLiveData<Boolean>()
    private val actionFinish = MutableLiveData<Boolean>()
    private var useMmolAsGlucoseUnits = true

    @Inject
    lateinit var dao: GlucoseLogDao

    @Inject
    lateinit var appSettings: AppSettings

    @Inject
    lateinit var appResources: AppResources

    init {
        if (dao == null) {
            App.appComponent.inject(this)
        } else {
            this.dao = dao
            if (appSettings != null) {
                this.appSettings = appSettings
            }
            if (appResources != null) {
                this.appResources = appResources
            }
        }

        useMmolAsGlucoseUnits = this.appSettings.useMmolAsGlucoseUnits()

        if (load) {
            loadData()
        } else {
            this.dateTime.value = MutableDateTime(dateTime)
            this.glucose.value = glucose
            this.measured.value = measured
        }
    }

    fun dateTimeObserver(): LiveData<MutableDateTime> = dateTime

    fun glucoseObserver(): LiveData<String> = glucose

    fun measuredObserver(): LiveData<Int> = measured

    fun errorLoadingObserver(): LiveData<Boolean> = errorLoading

    fun errorGlucoseEmptyObserver(): LiveData<Boolean> = errorGlucoseEmpty

    fun errorSaveObserver(): LiveData<Boolean> = errorSave

    fun errorDeleteObserver(): LiveData<Boolean> = errorDelete

    fun actionFinishObserver(): LiveData<Boolean> = actionFinish

    fun getId(): Long = id

    fun getCurrentDateTime(): DateTime = dateTime.value?.toDateTime() ?: DateTime()

    fun getGlucoseValue(): String = glucose.value ?: ""

    private fun getGlucoseValueMmol(): Float {
        val s = glucose.value
        if (s.isNullOrEmpty()) {
            return 0f
        }
        return s.toFloat()
    }

    private fun getGlucoseValueMg(): Int {
        val s = glucose.value
        if (s.isNullOrEmpty()) {
            return 0
        }
        var mg = s.toInt()
        if (mg < 0) {
            mg *= -1
        }
        return mg
    }

    fun getMeasured(): Int = measured.value ?: 0

    private fun loadData() {
        if (id != 0L) {
            dao.get(id).subscribeOn(io()).subscribe(object : SingleObserver<GlucoseLog> {
                override fun onSubscribe(d: Disposable) {}

                override fun onSuccess(l: GlucoseLog) {
                    dateTime.postValue(l.dateTime.toMutableDateTime())
                    glucose.postValue(
                        if (appSettings.useMmolAsGlucoseUnits()) l.valueMmol.toString()
                        else l.valueMg.toString()
                    )
                    measured.postValue(l.measured)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    errorLoading.postValue(true)
                }
            })
        } else {
            dateTime.postValue(MutableDateTime())
            glucose.postValue("")
            measured.postValue(0)
        }
    }

    fun save() {
        if (glucose.value.isNullOrEmpty()) {
            errorGlucoseEmpty.value = true
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

    private fun createLog(): GlucoseLog {
        var mmol = if (useMmolAsGlucoseUnits) {
            getGlucoseValueMmol()
        } else {
            intoMmol(getGlucoseValueMg())
        }
        mmol = roundFloat(mmol)

        val mg = if (useMmolAsGlucoseUnits) {
            intoMgDl(getGlucoseValueMmol())
        } else {
            getGlucoseValueMg()
        }
        return GlucoseLog(mmol, mg, getMeasured(), getCurrentDateTime()).also { it.id = id }
    }

    private fun intoMgDl(mmolL: Float?): Int {
        return ((mmolL ?: 0f) * 18f).toInt()
    }

    private fun intoMmol(mg: Int?): Float {
        return mg?.div(18f) ?: 0f
    }

    private fun roundFloat(num: Float): Float {
        return (num * 10.0f).roundToInt().toFloat() / 10.0f
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

    fun setGlucose(value: String) {
        glucose.value = value
        if (value.isNotEmpty()) {
            errorGlucoseEmpty.value = false
        }
    }

    fun setMeasured(measured: Int) {
        val maxMeasured = appResources.measuredArray().size - 1

        this.measured.value = when {
            measured < 0 -> 0
            measured > maxMeasured -> maxMeasured
            else -> measured
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