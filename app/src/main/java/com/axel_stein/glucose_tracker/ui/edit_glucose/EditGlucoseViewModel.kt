package com.axel_stein.glucose_tracker.ui.edit_glucose

import androidx.lifecycle.SavedStateHandle
import com.axel_stein.glucose_tracker.data.room.dao.GlucoseLogDao
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.App
import javax.inject.Inject

class EditGlucoseViewModel(id: Long = 0L, state: SavedStateHandle) : EditGlucoseViewModelImpl(id) {
    @Inject
    lateinit var _dao: GlucoseLogDao

    @Inject
    lateinit var _appSettings: AppSettings

    @Inject
    lateinit var _appResources: AppResources

    init {
        App.appComponent.inject(this)

        dateTime = state.getLiveData("date_time")
        glucose = state.getLiveData("glucose")
        measured = state.getLiveData("measured")
        errorLoading = state.getLiveData("error_loading")
        errorGlucoseEmpty = state.getLiveData("error_glucose_empty")
        errorSave = state.getLiveData("error_save")
        errorDelete = state.getLiveData("error_delete")
        actionFinish = state.getLiveData("action_finish")

        setDao(_dao)
        setAppSettings(_appSettings)
        setAppResources(_appResources)

        if (!state.contains("id")) {
            state["id"] = id
            loadData()
        }
    }
}