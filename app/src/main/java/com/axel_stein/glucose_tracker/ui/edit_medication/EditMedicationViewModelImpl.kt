package com.axel_stein.glucose_tracker.ui.edit_medication

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.model.Medication
import com.axel_stein.glucose_tracker.data.room.dao.MedicationDao
import com.axel_stein.glucose_tracker.utils.formatIfInt
import com.axel_stein.glucose_tracker.utils.getOrDefault
import com.axel_stein.glucose_tracker.utils.round
import io.reactivex.schedulers.Schedulers.io

open class EditMedicationViewModelImpl(protected val id: Long = 0L) : ViewModel() {
    protected var title = MutableLiveData<String>()
    protected var amount = MutableLiveData<String>()
    protected var dosageUnits = MutableLiveData<Int>()
    protected var errorEmptyTitle = MutableLiveData<Boolean>()
    protected var errorEmptyAmount = MutableLiveData<Boolean>()
    protected var actionFinish = MutableLiveData<Boolean>()
    protected lateinit var dao: MedicationDao

    fun titleLiveData(): LiveData<String> = title
    fun amountLiveData(): LiveData<String> = amount
    fun dosageLiveData(): LiveData<Int> = dosageUnits
    fun errorEmptyTitleLiveData(): LiveData<Boolean> = errorEmptyTitle
    fun errorEmptyAmountLiveData(): LiveData<Boolean> = errorEmptyAmount
    fun actionFinishLiveData(): LiveData<Boolean> = actionFinish

    @SuppressLint("CheckResult")
    fun loadData() {
        if (id != 0L) dao.get(id)
            .subscribeOn(io())
            .subscribe({ medication ->
                postData(
                    medication.title,
                    medication.amount.formatIfInt(),
                    medication.dosageUnits
                )
            }, {
                it.printStackTrace()
            })
        else postData()
    }

    private fun postData(title: String = "", amount: String = "", dosageUnits: Int = 0) {
        this.title.postValue(title)
        this.amount.postValue(amount)
        this.dosageUnits.postValue(dosageUnits)
    }

    fun setTitle(title: String) {
        this.title.value = title
        if (title.isNotBlank()) {
            errorEmptyTitle.value = false
        }
    }

    fun setAmount(amount: String) {
        this.amount.value = amount
        if (amount.isNotBlank()) {
            errorEmptyAmount.value = false
        }
    }

    fun setDosageUnits(dosageUnits: Int) {
        this.dosageUnits.value = dosageUnits
    }

    fun save() {
        when {
            title.value.isNullOrBlank() -> errorEmptyTitle.value = true
            amount.value.isNullOrBlank() -> errorEmptyAmount.value = true
            else -> {
                val medication = createMedication()
                val task = if (id != 0L) dao.update(medication) else dao.insert(medication)
                task.subscribeOn(io()).subscribe(
                    { actionFinish.postValue(true) },
                    { it.printStackTrace() }
                )
            }
        }
    }

    private fun createMedication() =
        Medication(
            title.getOrDefault(""),
            amount.getOrDefault("0").toFloat().round(),
            dosageUnits.getOrDefault(0)
        ).also { it.id = id }

    fun delete() {
        if (id != 0L) dao.deleteById(id)
            .subscribeOn(io())
            .subscribe(
                { actionFinish.postValue(true) },
                { it.printStackTrace() }
            )
    }
}