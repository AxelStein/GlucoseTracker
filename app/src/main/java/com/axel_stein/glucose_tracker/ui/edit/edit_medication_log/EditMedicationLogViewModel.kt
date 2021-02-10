package com.axel_stein.glucose_tracker.ui.edit.edit_medication_log

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.room.model.Medication
import com.axel_stein.glucose_tracker.data.room.model.MedicationLog
import com.axel_stein.glucose_tracker.data.room.dao.MedicationDao
import com.axel_stein.glucose_tracker.data.room.dao.MedicationLogDao
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.utils.formatRoundIfInt
import com.axel_stein.glucose_tracker.utils.get
import com.axel_stein.glucose_tracker.utils.getOrDefault
import com.axel_stein.glucose_tracker.utils.ui.Event
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import javax.inject.Inject

class EditMedicationLogViewModel(private val id: Long = 0L, private val state: SavedStateHandle) : ViewModel() {
    private val dateTime = MutableLiveData<MutableDateTime>()
    val dateTimeLiveData: LiveData<MutableDateTime> = dateTime

    private val amount = MutableLiveData<String>()
    val amountLiveData: LiveData<String> = amount

    private val medicationSelected = MutableLiveData<Int>()
    val medicationSelectedLiveData: LiveData<Int> = medicationSelected

    private val measured = MutableLiveData<Int>()
    val measuredLiveData: LiveData<Int> = measured

    private val dosageForm = MutableLiveData<Int>()
    val dosageFormLiveData: LiveData<Int> = dosageForm

    private val medicationList = MutableLiveData<List<Medication>>()
    val medicationListLiveData: LiveData<List<Medication>> = medicationList

    private val editorActive = MutableLiveData<Boolean>()
    val editorActiveLiveData: LiveData<Boolean> = editorActive

    private val errorLoading = MutableLiveData<Boolean>()
    val errorLoadingLiveData: LiveData<Boolean> = errorLoading

    private val errorAmountEmpty = MutableLiveData<Boolean>()
    val errorAmountEmptyLiveData: LiveData<Boolean> = errorAmountEmpty

    private val showMessage = MutableLiveData<Event<Int>>()
    val showMessageLiveData: LiveData<Event<Int>> = showMessage

    private val actionFinish = MutableLiveData<Event<Boolean>>()
    val actionFinishLiveData: LiveData<Event<Boolean>> = actionFinish

    private lateinit var dao: MedicationDao
    private lateinit var logDao: MedicationLogDao
    private val disposables = CompositeDisposable()

    init {
        App.appComponent.inject(this)
        loadData()
    }

    @Inject
    fun setDao(dao: MedicationDao, logDao: MedicationLogDao) {
        this.dao = dao
        this.logDao = logDao
    }

    fun getCurrentDateTime(): DateTime = dateTime.value?.toDateTime() ?: DateTime()

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        dateTime.value = dateTime.value.apply {
            this?.year = year
            this?.monthOfYear = month
            this?.dayOfMonth = dayOfMonth
        }
        state["date_time"] = dateTime.value
    }

    fun setTime(hourOfDay: Int, minuteOfHour: Int) {
        dateTime.value = dateTime.value.apply {
            this?.hourOfDay = hourOfDay
            this?.minuteOfHour = minuteOfHour
        }
        state["date_time"] = dateTime.value
    }

    fun selectMedication(position: Int) {
        medicationSelected.value = position
        state["medication_selected"] = position
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
        state["measured"] = position
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
        disposables.dispose()
    }

    @SuppressLint("CheckResult")
    private fun loadActiveMedications(medicationId: Long = -1L) {
        dao.getActiveItems()
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe({ items ->
                medicationList.value = items
                val restoredSelection = state.get<Int>("medication_selected")
                if (items.isEmpty()) {
                    editorActive.value = false
                } else if (restoredSelection == null) {
                    if (medicationId == -1L) selectMedication(0)
                    else items.forEachIndexed { index, medication ->
                        if (medication.id == medicationId) {
                            selectMedication(index)
                            return@forEachIndexed
                        }
                    }
                } else {
                    selectMedication(restoredSelection)
                }
            }, {
                it.printStackTrace()
                errorLoading.value = true
            })
    }

    @SuppressLint("CheckResult")
    fun loadData() {
        if (id == 0L) {
            setData()
            loadActiveMedications()
        } else logDao.getById(id)
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe({
                setData(
                    it.log.dateTime.toMutableDateTime(),
                    it.log.amount.formatRoundIfInt(),
                    it.log.measured
                )
                if (!it.medication.active) {
                    medicationList.value = listOf(it.medication)
                    selectMedication(0)
                    editorActive.value = false
                } else {
                    loadActiveMedications(it.medication.id)
                }
            }, {
                it.printStackTrace()
                errorLoading.value = true
            })
    }

    private fun setData(
        dateTime: MutableDateTime = MutableDateTime.now(),
        amount: String = "1",
        measured: Int = 0
    ) {
        this.dateTime.value = state.get("date_time") ?: dateTime
        this.amount.value = state.get("amount") ?: amount
        selectMeasured(state.get("measured") ?: measured)
    }

    @SuppressLint("CheckResult")
    fun save() {
        when {
            amount.value.isNullOrBlank() -> {
                errorAmountEmpty.value = true
            }
            else -> {
                Completable.fromAction {
                    logDao.upsert(createLog())
                }.subscribeOn(io()).subscribe({
                    actionFinish.postValue(Event())
                }, {
                    it.printStackTrace()
                    showMessage.postValue(Event(R.string.error_saving_log))
                })
            }
        }
    }

    private fun createLog(): MedicationLog {
        val items = medicationList.getOrDefault(emptyList())
        if (items.isEmpty()) {
            throw IllegalStateException("Medication list is empty")
        }
        val insulin = items[medicationSelected.getOrDefault(0)]
        return MedicationLog(
            insulin.id,
            amount.getOrDefault("1").toFloat(),
            measured.getOrDefault(0),
            dateTime.getOrDefault(MutableDateTime()).toDateTime()
        ).also { it.id = id }
    }

    @SuppressLint("CheckResult")
    fun delete() {
        if (id != 0L) {
            logDao.deleteById(id).subscribeOn(io()).subscribe(
                { actionFinish.postValue(Event()) },
                {
                    it.printStackTrace()
                    showMessage.postValue(Event(R.string.error_saving_log))
                }
            )
        }
    }
}