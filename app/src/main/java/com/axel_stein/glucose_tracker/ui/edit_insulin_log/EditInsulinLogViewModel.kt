package com.axel_stein.glucose_tracker.ui.edit_insulin_log

import androidx.lifecycle.SavedStateHandle

open class EditInsulinLogViewModel(id: Long = 0L, private val state: SavedStateHandle) : EditInsulinLogViewModelImpl(id) {
    init {
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