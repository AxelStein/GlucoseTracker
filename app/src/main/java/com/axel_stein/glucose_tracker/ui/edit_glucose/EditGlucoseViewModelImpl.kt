package com.axel_stein.glucose_tracker.ui.edit_glucose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.model.GlucoseLog
import com.axel_stein.glucose_tracker.data.room.dao.GlucoseLogDao
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import kotlin.math.roundToInt

open class EditGlucoseViewModelImpl(private var id: Long = 0L) : ViewModel() {
    protected var dateTime = MutableLiveData<MutableDateTime>()
    protected var glucose = MutableLiveData<String>()
    protected var measured = MutableLiveData<Int>()
    protected var errorLoading = MutableLiveData<Boolean>()
    protected var errorGlucoseEmpty = MutableLiveData<Boolean>()
    protected var errorSave = MutableLiveData<Boolean>()
    protected var errorDelete = MutableLiveData<Boolean>()
    protected var actionFinish = MutableLiveData<Boolean>()
    private var useMmolAsGlucoseUnits = true
    private lateinit var dao: GlucoseLogDao
    private lateinit var appSettings: AppSettings
    private lateinit var appResources: AppResources

    fun setDao(dao: GlucoseLogDao) {
        this.dao = dao
    }

    fun setAppSettings(appSettings: AppSettings) {
        this.appSettings = appSettings
        useMmolAsGlucoseUnits = this.appSettings.useMmolAsGlucoseUnits()
    }

    fun setAppResources(appResources: AppResources) {
        this.appResources = appResources
    }

    fun dateTimeLiveData(): LiveData<MutableDateTime> = dateTime

    fun glucoseLiveData(): LiveData<String> = glucose

    fun measuredLiveData(): LiveData<Int> = measured

    fun errorLoadingLiveData(): LiveData<Boolean> = errorLoading

    fun errorGlucoseEmptyLiveData(): LiveData<Boolean> = errorGlucoseEmpty

    fun errorSaveLiveData(): LiveData<Boolean> = errorSave

    fun errorDeleteLiveData(): LiveData<Boolean> = errorDelete

    fun actionFinishLiveData(): LiveData<Boolean> = actionFinish

    fun getCurrentDateTime(): DateTime = dateTime.value?.toDateTime() ?: DateTime()

    fun getGlucoseValue(): String = glucose.value ?: ""

    private fun getGlucoseValueMmol(): Float {
        val s = glucose.value
        if (s.isNullOrEmpty()) return 0f
        val num = s.toFloat()
        return if (num < 0) num * -1.0f else num
    }

    private fun getGlucoseValueMg(): Int {
        val s = glucose.value
        if (s.isNullOrEmpty()) return 0
        val num = s.toInt()
        return if (num < 0) num * -1 else num
    }

    fun getMeasured(): Int = measured.value ?: 0

    fun loadData() {
        if (id != 0L) {
            dao.get(id).subscribeOn(Schedulers.io()).subscribe(object : SingleObserver<GlucoseLog> {
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

    private fun intoMgDl(mmolL: Float?): Int = ((mmolL ?: 0f) * 18f).toInt()

    private fun intoMmol(mg: Int?): Float = mg?.div(18f) ?: 0f

    private fun roundFloat(num: Float): Float = (num * 10.0f).roundToInt().toFloat() / 10.0f

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
        glucose.value = value.replace(',', '.')
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