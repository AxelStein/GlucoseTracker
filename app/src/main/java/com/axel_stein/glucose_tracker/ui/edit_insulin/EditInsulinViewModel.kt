package com.axel_stein.glucose_tracker.ui.edit_insulin

import androidx.lifecycle.SavedStateHandle

class EditInsulinViewModel(id: Long = 0L, state: SavedStateHandle) : EditInsulinViewModelImpl(id) {
    init {
        title = state.getLiveData("title")
        type = state.getLiveData("type")
        errorEmptyTitle = state.getLiveData("error_empty_title")
        actionFinish = state.getLiveData("action_finish")

        if (!state.contains("id")) {
            state["id"] = id
            loadData()
        }
    }
}