package com.axel_stein.glucose_tracker.ui.edit_medication

import androidx.lifecycle.SavedStateHandle
import com.axel_stein.glucose_tracker.data.room.dao.MedicationDao
import com.axel_stein.glucose_tracker.ui.App
import javax.inject.Inject

class EditMedicationViewModel(id: Long, state: SavedStateHandle) : EditMedicationViewModelImpl(id) {
    @Inject
    lateinit var _dao: MedicationDao

    init {
        App.appComponent.inject(this)

        dao = _dao

        title = state.getLiveData("title")
        dosage = state.getLiveData("dosage")
        dosageUnit = state.getLiveData("units")
        errorEmptyTitle = state.getLiveData("error_empty_title")
        actionFinish = state.getLiveData("action_finish")

        if (!state.contains("id")) {
            state["id"] = id
            loadData()
        }
    }
}