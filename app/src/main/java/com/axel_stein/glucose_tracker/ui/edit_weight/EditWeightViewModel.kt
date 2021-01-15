package com.axel_stein.glucose_tracker.ui.edit_weight

import androidx.lifecycle.SavedStateHandle
import com.axel_stein.glucose_tracker.data.room.dao.WeightLogDao
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.App
import javax.inject.Inject

class EditWeightViewModel(id: Long = 0L, state: SavedStateHandle) : EditWeightViewModelImpl(id) {
    @Inject
    lateinit var _dao: WeightLogDao

    @Inject
    lateinit var _appSettings: AppSettings

    init {
        App.appComponent.inject(this)
        setDao(_dao)
        setAppSettings(_appSettings)

        dateTime = state.getLiveData("date_time")
        weight = state.getLiveData("weight")
        errorSave = state.getLiveData("error_save")
        errorDelete = state.getLiveData("error_delete")
        actionFinish = state.getLiveData("action_finish")

        if (!state.contains("id")) {
            state["id"] = id
            loadData()
        }
    }
}