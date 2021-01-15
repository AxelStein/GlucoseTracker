package com.axel_stein.glucose_tracker.ui.edit_glucose

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.model.GlucoseLog
import com.axel_stein.glucose_tracker.data.room.dao.GlucoseLogDao
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.utils.getOrDefault
import com.axel_stein.glucose_tracker.utils.intoMgDl
import com.axel_stein.glucose_tracker.utils.intoMmol
import com.axel_stein.glucose_tracker.utils.round
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import kotlin.math.absoluteValue

open class EditGlucoseViewModelImpl(private var id: Long = 0L) : ViewModel() {
    protected var dateTime = MutableLiveData<MutableDateTime>()
    protected var glucose = MutableLiveData<String>()
    protected var measured = MutableLiveData<Int>()
    protected var errorLoading = MutableLiveData<Boolean>()
    protected var errorGlucoseEmpty = MutableLiveData<Boolean>()
    protected var errorSave = MutableLiveData<Boolean>()
    protected var errorDelete = MutableLiveData<Boolean>()
    protected var actionFinish = MutableLiveData<Boolean>()
    private var useMmol = true
    private lateinit var dao: GlucoseLogDao
    private lateinit var appSettings: AppSettings
    private lateinit var appResources: AppResources

    fun setDao(dao: GlucoseLogDao) {
        this.dao = dao
    }

    fun setAppSettings(appSettings: AppSettings) {
        this.appSettings = appSettings
        useMmol = this.appSettings.useMmolAsGlucoseUnits()
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

    fun getCurrentDateTime(): DateTime = dateTime.getOrDefault(MutableDateTime()).toDateTime()

    fun getGlucoseValue(): String = glucose.getOrDefault("")

    private fun glucoseValueMmol() = glucose.getOrDefault("0").toFloat().absoluteValue

    private fun glucoseValueMg() = glucose.getOrDefault("0").toInt().absoluteValue

    fun getMeasured(): Int = measured.getOrDefault(0)

    fun loadData() {
        if (id == 0L) postData()
        else dao.get(id)
            .subscribeOn(io())
            .subscribe({ log ->
                val logGlucose = if (useMmol) log.valueMmol else log.valueMg
                postData(
                    log.dateTime.toMutableDateTime(),
                    logGlucose.toString(),
                    log.measured
                )
            }, {
                it.printStackTrace()
                errorLoading.postValue(true)
            })
    }

    private fun postData(dateTime: MutableDateTime = MutableDateTime(), glucose: String = "", measured: Int = 0) {
        this.dateTime.postValue(dateTime)
        this.glucose.postValue(glucose)
        this.measured.postValue(measured)
    }

    @SuppressLint("CheckResult")
    fun save() {
        if (glucose.value.isNullOrEmpty()) {
            errorGlucoseEmpty.value = true
        } else {
            val log = createLog()
            val task = if (id != 0L) dao.update(log) else dao.insert(log)
            task.subscribeOn(io()).subscribe(
                { actionFinish.postValue(true) },
                {
                    it.printStackTrace()
                    errorSave.postValue(true)
                }
            )
        }
    }

    private fun createLog(): GlucoseLog {
        val mmol = (if (useMmol) glucoseValueMmol() else glucoseValueMg().intoMmol()).round()
        val mg = if (useMmol) glucoseValueMmol().intoMgDl() else glucoseValueMg()
        return GlucoseLog(mmol, mg, getMeasured(), getCurrentDateTime()).also { it.id = id }
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
        glucose.value = value.replace(',', '.')
        if (value.isNotEmpty()) {
            errorGlucoseEmpty.value = false
        }
    }

    fun setMeasured(measured: Int) {
        val maxMeasured = appResources.measuredArray.size - 1

        this.measured.value = when {
            measured < 0 -> 0
            measured > maxMeasured -> maxMeasured
            else -> measured
        }
    }

    @SuppressLint("CheckResult")
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