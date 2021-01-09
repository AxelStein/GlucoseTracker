package com.axel_stein.glucose_tracker.ui.edit_note

import androidx.lifecycle.SavedStateHandle
import com.axel_stein.glucose_tracker.data.room.dao.NoteLogDao
import com.axel_stein.glucose_tracker.ui.App
import javax.inject.Inject

class EditNoteViewModel(id: Long = 0L, state: SavedStateHandle) : EditNoteViewModelImpl(id) {
    @Inject
    lateinit var noteLogDao: NoteLogDao

    init {
        App.appComponent.inject(this)
        setDao(noteLogDao)

        dateTime = state.getLiveData("date_time")
        note = state.getLiveData("note")
        errorNoteEmpty = state.getLiveData("error_note")
        errorSave = state.getLiveData("error_save")
        errorDelete = state.getLiveData("error_delete")
        actionFinish = state.getLiveData("action_finish")

        if (!state.contains("id")) {
            state["id"] = id
            loadData()
        }
    }
}