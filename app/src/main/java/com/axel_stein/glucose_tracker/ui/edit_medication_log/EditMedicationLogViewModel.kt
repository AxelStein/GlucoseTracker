package com.axel_stein.glucose_tracker.ui.edit_medication_log

import androidx.lifecycle.SavedStateHandle
import com.axel_stein.glucose_tracker.data.room.dao.MedicationDao
import com.axel_stein.glucose_tracker.data.room.dao.MedicationLogDao
import com.axel_stein.glucose_tracker.ui.App
import javax.inject.Inject

class EditMedicationLogViewModel(id: Long = 0L, state: SavedStateHandle) : EditMedicationLogViewModelImpl(id) {
    @Inject
    lateinit var _logDao: MedicationLogDao

    @Inject
    lateinit var _listDao: MedicationDao

    init {
        App.appComponent.inject(this)
        logDao = _logDao
        listDao = _listDao

        dateTime = state.getLiveData("date_time")
        medicationSelected = state.getLiveData("medication_selected")
        medicationList = state.getLiveData("medication_list")
        editorActive = state.getLiveData("editor_active")
        amount = state.getLiveData("amount")
        measured = state.getLiveData("measured")
        errorLoading = state.getLiveData("error_loading")
        errorSave = state.getLiveData("error_save")
        errorDelete = state.getLiveData("error_delete")
        actionFinish = state.getLiveData("action_finish")

        if (!state.contains("id")) {
            state["id"] = id
            loadData()
        }
    }
}