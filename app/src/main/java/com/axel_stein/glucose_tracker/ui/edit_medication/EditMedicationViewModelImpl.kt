package com.axel_stein.glucose_tracker.ui.edit_medication

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.model.Medication
import com.axel_stein.glucose_tracker.data.room.dao.MedicationDao
import com.axel_stein.glucose_tracker.utils.formatIfInt
import com.axel_stein.glucose_tracker.utils.getOrDefault
import com.axel_stein.glucose_tracker.utils.notBlankOrDefault
import io.reactivex.schedulers.Schedulers.io

open class EditMedicationViewModelImpl(protected val id: Long = 0L) : ViewModel() {
    protected var title = MutableLiveData<String>()
    protected var dosageForm = MutableLiveData<Int>()
    protected var dosage = MutableLiveData<String>()
    protected var dosageUnit = MutableLiveData<Int>()
    protected var active = MutableLiveData<Boolean>()
    protected var errorEmptyTitle = MutableLiveData<Boolean>()
    protected var actionFinish = MutableLiveData<Boolean>()
    protected lateinit var dao: MedicationDao

    fun titleLiveData(): LiveData<String> = title
    fun dosageFormLiveData(): LiveData<Int> = dosageForm
    fun dosageLiveData(): LiveData<String> = dosage
    fun dosageUnitLiveData(): LiveData<Int> = dosageUnit
    fun activeLiveData(): LiveData<Boolean> = active
    fun errorEmptyTitleLiveData(): LiveData<Boolean> = errorEmptyTitle
    fun actionFinishLiveData(): LiveData<Boolean> = actionFinish

    @SuppressLint("CheckResult")
    fun loadData() {
        if (id != 0L) dao.get(id)
            .subscribeOn(io())
            .subscribe({ medication ->
                var dosage = medication.dosage.formatIfInt()
                if (dosage == "0") dosage = ""
                postData(
                    medication.title,
                    medication.dosageForm,
                    dosage,
                    medication.dosageUnit,
                    medication.active
                )
            }, {
                it.printStackTrace()
            })
        else postData()
    }

    private fun postData(
        title: String = "",
        dosageForm: Int = 0,
        dosage: String = "",
        dosageUnits: Int = -1,
        active: Boolean = true
    ) {
        this.title.postValue(title)
        this.dosageForm.postValue(dosageForm)
        this.dosage.postValue(dosage)
        this.dosageUnit.postValue(dosageUnits)
        this.active.postValue(active)
    }

    fun setTitle(title: String) {
        this.title.value = title
        if (title.isNotBlank()) {
            errorEmptyTitle.value = false
        }
    }

    fun setDosageForm(form: Int) {
        this.dosageForm.value = form
    }

    fun setDosage(dosage: String) {
        this.dosage.value = dosage
    }

    fun setDosageUnit(dosageUnit: Int) {
        this.dosageUnit.value = dosageUnit
    }

    @SuppressLint("CheckResult")
    fun toggleActive() {
        val updatedValue = !active.getOrDefault(true)
        dao.setActive(id, updatedValue)
            .subscribeOn(io())
            .subscribe({
                active.postValue(updatedValue)
                actionFinish.postValue(true)
            }, {
                it.printStackTrace()
            })
    }

    fun save() {
        when {
            title.value.isNullOrBlank() -> errorEmptyTitle.value = true
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

    private fun createMedication(): Medication {
        val medication = Medication(
            title.getOrDefault(""),
            dosageForm.getOrDefault(0),
            dosage.notBlankOrDefault("0").toFloat(),
            dosageUnit.getOrDefault(-1),
            active.getOrDefault(true)
        ).also { it.id = id }

        if (medication.dosage == 0f) {
            medication.dosageUnit = -1
        }
        if (medication.dosageUnit == -1) {
            medication.dosage = 0f
        }
        return medication
    }

    fun delete() {
        if (id != 0L) dao.deleteById(id)
            .subscribeOn(io())
            .subscribe(
                { actionFinish.postValue(true) },
                { it.printStackTrace() }
            )
    }
}