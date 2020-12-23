package com.axel_stein.glucose_tracker.ui.edit_glucose

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class EdiGlucoseFactory(private val id: Long, private val state: Bundle?): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditGlucoseViewModel::class.java)) {
            return if (state == null) {
                EditGlucoseViewModel(id) as T
            } else {
                val id = state.getLong(EditGlucoseActivity.EXTRA_ID)
                val dateTime = state.getString(EditGlucoseActivity.EXTRA_DATE_TIME)
                val glucose = state.getString(EditGlucoseActivity.EXTRA_GLUCOSE, "")
                val measured = state.getInt(EditGlucoseActivity.EXTRA_MEASURED, 0)
                EditGlucoseViewModel(id, false, glucose, measured, dateTime) as T
            }
        } else {
            throw IllegalArgumentException()
        }
    }
}