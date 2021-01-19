package com.axel_stein.glucose_tracker.ui.edit_medication

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.model.Medication
import com.axel_stein.glucose_tracker.data.room.dao.MedicationDao
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.utils.formatIfInt
import com.axel_stein.glucose_tracker.utils.getOrDefault
import com.axel_stein.glucose_tracker.utils.notBlankOrDefault
import com.axel_stein.glucose_tracker.utils.ui.Event
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class EditMedicationViewModel(private val id: Long = 0L, private val state: SavedStateHandle) : ViewModel() {
    private val title = MutableLiveData<String>()
    val titleLiveData: LiveData<String> = title

    private val dosageForm = MutableLiveData<Int>()
    val dosageFormLiveData: LiveData<Int> = dosageForm

    private val dosage = MutableLiveData<String>()
    val dosageLiveData: LiveData<String> = dosage

    private val dosageUnit = MutableLiveData<Int>()
    val dosageUnitLiveData: LiveData<Int> = dosageUnit

    private val active = MutableLiveData<Boolean>()
    val activeLiveData: LiveData<Boolean> = active

    private val actionFinish = MutableLiveData<Event<Boolean>>()
    val actionFinishLiveData: LiveData<Event<Boolean>> = actionFinish

    private val errorEmptyTitle = MutableLiveData<Boolean>()
    val errorEmptyTitleLiveData: LiveData<Boolean> = errorEmptyTitle

    private val showMessage = MutableLiveData<Event<Int>>()
    val showMessageLiveData: LiveData<Event<Int>> = showMessage

    private lateinit var dao: MedicationDao

    init {
        App.appComponent.inject(this)
        loadData()
    }

    @Inject
    fun setDao(dao: MedicationDao) {
        this.dao = dao
    }

    @SuppressLint("CheckResult")
    fun loadData() {
        if (id != 0L) dao.getById(id)
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe({ medication ->
                var dosage = medication.dosage.formatIfInt()
                if (dosage == "0") dosage = ""
                setData(
                    medication.title,
                    medication.dosageForm,
                    dosage,
                    medication.dosageUnit,
                    medication.active
                )
            }, {
                it.printStackTrace()
            })
        else setData()
    }

    private fun setData(
        title: String = "",
        dosageForm: Int = 0,
        dosage: String = "",
        dosageUnit: Int = -1,
        active: Boolean = true
    ) {
        this.title.value = state.get("title") ?: title
        this.dosageForm.value = state.get("dosage_form") ?: dosageForm
        this.dosage.value = state.get("dosage") ?: dosage
        this.dosageUnit.value = state.get("dosage_unit") ?: dosageUnit
        this.active.value = active
    }

    fun setTitle(title: String) {
        this.title.value = title
        state["title"] = title
        if (title.isNotBlank()) {
            errorEmptyTitle.value = false
        }
    }

    fun setDosageForm(form: Int) {
        this.dosageForm.value = form
        state["dosage_form"] = form
    }

    fun setDosage(dosage: String) {
        this.dosage.value = dosage
        state["dosage"] = dosage
    }

    fun setDosageUnit(dosageUnit: Int) {
        this.dosageUnit.value = dosageUnit
        state["dosage_unit"] = dosageUnit
    }

    @SuppressLint("CheckResult")
    fun toggleActive() {
        val updatedValue = !active.getOrDefault(true)
        dao.setActive(id, updatedValue)
            .subscribeOn(io())
            .subscribe({
                actionFinish.postValue(Event())
            }, {
                it.printStackTrace()
                showMessage.postValue(Event(R.string.error_toggle_active))
            })
    }

    fun save() {
        when {
            title.value.isNullOrBlank() -> errorEmptyTitle.value = true
            else -> {
                Completable.fromAction {
                    dao.upsert(createMedication())
                }.subscribeOn(io()).subscribe({
                    actionFinish.postValue(Event())
                }, {
                    it.printStackTrace()
                    showMessage.postValue(Event(R.string.error_saving))
                })
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
                { actionFinish.postValue(Event()) },
                {
                    it.printStackTrace()
                    showMessage.postValue(Event(R.string.error_deleting))
                }
            )
    }
}