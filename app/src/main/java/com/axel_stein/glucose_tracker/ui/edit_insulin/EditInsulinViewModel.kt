package com.axel_stein.glucose_tracker.ui.edit_insulin

import androidx.lifecycle.SavedStateHandle
import com.axel_stein.glucose_tracker.data.room.dao.InsulinDao
import com.axel_stein.glucose_tracker.ui.App
import javax.inject.Inject

class EditInsulinViewModel(id: Long = 0L, state: SavedStateHandle) : EditInsulinViewModelImpl(id) {
    @Inject
    lateinit var _dao: InsulinDao

    init {
        App.appComponent.inject(this)
        dao = _dao

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