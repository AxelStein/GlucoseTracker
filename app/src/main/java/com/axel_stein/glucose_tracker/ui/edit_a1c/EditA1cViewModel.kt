package com.axel_stein.glucose_tracker.ui.edit_a1c

import androidx.lifecycle.SavedStateHandle
import com.axel_stein.glucose_tracker.data.room.dao.A1cLogDao
import com.axel_stein.glucose_tracker.ui.App
import javax.inject.Inject

class EditA1cViewModel(id: Long = 0L, state: SavedStateHandle) : EditA1cViewModelImpl(id) {
    @Inject
    lateinit var logDao: A1cLogDao

    init {
        App.appComponent.inject(this)
        setDao(logDao)

        dateTime = state.getLiveData("date_time")
        a1c = state.getLiveData("a1c")
        errorValueEmpty = state.getLiveData("error_value_empty")
        errorSave = state.getLiveData("error_save")
        errorDelete = state.getLiveData("error_delete")
        actionFinish = state.getLiveData("action_finish")

        if (!state.contains("id")) {
            state["id"] = id
            loadData()
        }
    }
}