package com.axel_stein.glucose_tracker.ui.edit_insulin_log

import androidx.lifecycle.SavedStateHandle
import com.axel_stein.glucose_tracker.data.room.dao.InsulinDao
import com.axel_stein.glucose_tracker.data.room.dao.InsulinLogDao
import com.axel_stein.glucose_tracker.ui.App
import javax.inject.Inject

class EditInsulinLogViewModel(id: Long = 0L, state: SavedStateHandle) : EditInsulinLogViewModelImpl(id) {

    @Inject
    lateinit var _logDao: InsulinLogDao

    @Inject
    lateinit var _listDao: InsulinDao

    init {
        App.appComponent.inject(this)
        logDao = _logDao
        listDao = _listDao

        dateTime = state.getLiveData("date_time")
        insulinSelected = state.getLiveData("insulin_selected")
        insulinList = state.getLiveData("insulin_list")
        units = state.getLiveData("units")
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