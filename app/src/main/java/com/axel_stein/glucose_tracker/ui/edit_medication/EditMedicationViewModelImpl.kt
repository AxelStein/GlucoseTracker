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
    protected var dosage = MutableLiveData<String>()
    protected var units = MutableLiveData<Int>()
    protected var errorEmptyTitle = MutableLiveData<Boolean>()
    protected var errorEmptyDosage = MutableLiveData<Boolean>()
    protected var actionFinish = MutableLiveData<Boolean>()
    protected lateinit var dao: MedicationDao

    fun titleLiveData(): LiveData<String> = title
    fun dosageLiveData(): LiveData<String> = dosage
    fun unitsLiveData(): LiveData<Int> = units
    fun errorEmptyTitleLiveData(): LiveData<Boolean> = errorEmptyTitle
    fun errorEmptyDosageLiveData(): LiveData<Boolean> = errorEmptyDosage
    fun actionFinishLiveData(): LiveData<Boolean> = actionFinish

    @SuppressLint("CheckResult")
    fun loadData() {
        if (id != 0L) dao.get(id)
            .subscribeOn(io())
            .subscribe({ medication ->
                postData(
                    medication.title,
                    medication.dosage.formatIfInt(),
                    medication.units
                )
            }, {
                it.printStackTrace()
            })
        else postData()
    }

    private fun postData(title: String = "", amount: String = "", dosageUnits: Int = 0) {
        this.title.postValue(title)
        this.dosage.postValue(amount)
        this.units.postValue(dosageUnits)
    }

    fun setTitle(title: String) {
        this.title.value = title
        if (title.isNotBlank()) {
            errorEmptyTitle.value = false
        }
    }

    fun setAmount(amount: String) {
        this.dosage.value = amount
        if (amount.isNotBlank()) {
            errorEmptyDosage.value = false
        }
    }

    fun setDosageUnits(dosageUnits: Int) {
        this.units.value = dosageUnits
    }

    fun save() {
        when {
            title.value.isNullOrBlank() -> errorEmptyTitle.value = true
            dosage.value.isNullOrBlank() -> errorEmptyDosage.value = true
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
            dosage.getOrDefault("0").toFloat().round(),
            units.getOrDefault(0)
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