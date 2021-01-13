package com.axel_stein.glucose_tracker.ui.edit_medication_log

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.model.Medication
import com.axel_stein.glucose_tracker.data.model.MedicationLog
import com.axel_stein.glucose_tracker.data.room.dao.MedicationDao
import com.axel_stein.glucose_tracker.data.room.dao.MedicationLogDao
import com.axel_stein.glucose_tracker.utils.get
import com.axel_stein.glucose_tracker.utils.getOrDefault
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.DateTime
import org.joda.time.MutableDateTime

open class EditMedicationLogViewModelImpl(private val id: Long = 0L) : ViewModel() {
    protected var dateTime = MutableLiveData<MutableDateTime>()
    protected var medicationList = MutableLiveData<List<Medication>>()
    protected var medicationSelected = MutableLiveData<Int>()
    protected var amount = MutableLiveData<String>()
    protected var dosageForm = MutableLiveData<Int>()
    protected var measured = MutableLiveData<Int>()
    protected var errorLoading = MutableLiveData<Boolean>()
    protected var errorMedicationListEmpty = MutableLiveData<Boolean>()
    protected var errorAmountEmpty = MutableLiveData<Boolean>()
    protected var errorSave = MutableLiveData<Boolean>()
    protected var errorDelete = MutableLiveData<Boolean>()
    protected var actionFinish = MutableLiveData<Boolean>()
    protected lateinit var logDao: MedicationLogDao
    protected lateinit var listDao: MedicationDao
    private val disposables = CompositeDisposable()

    fun dateTimeLiveData(): LiveData<MutableDateTime> = dateTime
    fun medicationListLiveData(): LiveData<List<Medication>> = medicationList
    fun medicationSelectedLiveData(): LiveData<Int> = medicationSelected
    fun amountLiveData(): LiveData<String> = amount
    fun dosageFormLiveData(): LiveData<Int> = dosageForm
    fun measuredLiveData(): LiveData<Int> = measured
    fun errorLoadingLiveData(): LiveData<Boolean> = errorLoading
    fun errorAmountEmptyLiveData(): LiveData<Boolean> = errorAmountEmpty
    fun errorMedicationListEmptyLiveData(): LiveData<Boolean> = errorMedicationListEmpty
    fun errorSaveLiveData(): LiveData<Boolean> = errorSave
    fun errorDeleteLiveData(): LiveData<Boolean> = errorDelete
    fun actionFinishLiveData(): LiveData<Boolean> = actionFinish
    fun getCurrentDateTime(): DateTime = dateTime.value?.toDateTime() ?: DateTime()

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

    open fun selectMedication(position: Int) {
        medicationSelected.value = position
        if (!medicationList.value.isNullOrEmpty()) {
            if (position >= 0) {
                val medication = medicationList.get()[position]
                dosageForm.postValue(medication.dosageForm)
            }
        }
    }

    fun setAmount(amount: String) {
        this.amount.value = amount
        if (amount.isNotBlank()) {
            errorAmountEmpty.value = false
        }
    }

    fun selectMeasured(position: Int) {
        this.measured.value = position
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
        disposables.dispose()
    }

    @SuppressLint("CheckResult")
    private fun loadMedicationList(loadLogCallback: (List<Medication>) -> Unit) {
        disposables.add(
            listDao.observeActiveItems()
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribe({ items ->
                    medicationList.value = items
                    if (items.isNotEmpty()) {
                        errorMedicationListEmpty.value = false
                    }
                    if (dateTime.value == null) {
                        loadLogCallback(items)
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }

    @SuppressLint("CheckResult")
    fun loadData() {
        loadMedicationList { medications ->
            if (id == 0L) setData(selectedMedication = if (medications.isNotEmpty()) 0 else -1)
            else logDao.get(id)
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribe({ log ->
                    val selected = if (medications.isNullOrEmpty()) -1 else {
                        medications.indexOf(
                            medications.find { item -> item.id == log.id }
                        )
                    }
                    setData(
                        log.dateTime.toMutableDateTime(),
                        log.amount.toString(),
                        log.measured,
                        selected
                    )
                }, {
                    it.printStackTrace()
                })
        }
    }

    private fun setData(
        dateTime: MutableDateTime = MutableDateTime.now(),
        amount: String = "1",
        measured: Int = 0,
        selectedMedication: Int = -1
    ) {
        this.dateTime.value = dateTime
        this.amount.value = amount
        this.measured.value = measured
        selectMedication(selectedMedication)
    }

    @SuppressLint("CheckResult")
    fun save() {
        when {
            medicationList.value.isNullOrEmpty() -> {
                errorMedicationListEmpty.value = true
            }
            amount.value.isNullOrBlank() -> {
                errorAmountEmpty.value = true
            }
            else -> {
                createLog()
                    .subscribeOn(io())
                    .map { log ->
                        if (id != 0L) logDao.update(log)
                        else logDao.insert(log)
                    }
                    .subscribe(
                        { actionFinish.postValue(true) },
                        { it.printStackTrace() }
                    )
            }
        }
    }

    private fun createLog() = Single.fromCallable {
        val items = medicationList.getOrDefault(emptyList())
        if (items.isEmpty()) {
            throw IllegalStateException("Medication list is empty")
        }
        val insulin = items[medicationSelected.getOrDefault(0)]
        MedicationLog(
            insulin.id,
            amount.getOrDefault("1").toFloat(),
            measured.getOrDefault(0),
            dateTime.getOrDefault(MutableDateTime()).toDateTime()
        )
    }

    @SuppressLint("CheckResult")
    fun delete() {
        if (id != 0L) {
            logDao.deleteById(id).subscribeOn(io()).subscribe(
                { actionFinish.postValue(true) },
                { it.printStackTrace() }
            )
        }
    }
}