package com.axel_stein.glucose_tracker.ui.edit_note

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.axel_stein.glucose_tracker.ui.edit_note.EditNoteActivity.Companion.EXTRA_DATE_TIME
import com.axel_stein.glucose_tracker.ui.edit_note.EditNoteActivity.Companion.EXTRA_ID
import com.axel_stein.glucose_tracker.ui.edit_note.EditNoteActivity.Companion.EXTRA_NOTE

@Suppress("UNCHECKED_CAST")
class EditNoteFactory(private val id: Long, private val state: Bundle?): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditNoteViewModel::class.java)) {
            return if (state == null) {
                EditNoteViewModel(id) as T
            } else {
                val id = state.getLong(EXTRA_ID)
                val note = state.getString(EXTRA_NOTE, "")
                val dateTime = state.getString(EXTRA_DATE_TIME)
                EditNoteViewModel(id, false, note, dateTime) as T
            }
        } else {
            throw IllegalArgumentException()
        }
    }
}