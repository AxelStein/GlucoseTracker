package com.axel_stein.glucose_tracker.ui.edit_glucose

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.axel_stein.glucose_tracker.ui.edit_glucose.EditGlucoseActivity.Companion.EXTRA_DATE_TIME
import com.axel_stein.glucose_tracker.ui.edit_glucose.EditGlucoseActivity.Companion.EXTRA_GLUCOSE
import com.axel_stein.glucose_tracker.ui.edit_glucose.EditGlucoseActivity.Companion.EXTRA_ID
import com.axel_stein.glucose_tracker.ui.edit_glucose.EditGlucoseActivity.Companion.EXTRA_MEASURED

@Suppress("UNCHECKED_CAST")
class EditGlucoseFactory(private val id: Long, private val state: Bundle?): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditGlucoseViewModel::class.java)) {
            return if (state == null) {
                EditGlucoseViewModel(id) as T
            } else {
                val id = state.getLong(EXTRA_ID)
                val dateTime = state.getString(EXTRA_DATE_TIME)
                val glucose = state.getString(EXTRA_GLUCOSE, "")
                val measured = state.getInt(EXTRA_MEASURED, 0)
                EditGlucoseViewModel(id, false, glucose, measured, dateTime) as T
            }
        } else {
            throw IllegalArgumentException()
        }
    }
}