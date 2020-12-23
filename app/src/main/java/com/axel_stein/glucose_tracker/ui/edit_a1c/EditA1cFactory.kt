package com.axel_stein.glucose_tracker.ui.edit_a1c

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.axel_stein.glucose_tracker.ui.edit_a1c.EditA1cActivity.Companion.EXTRA_A1C
import com.axel_stein.glucose_tracker.ui.edit_a1c.EditA1cActivity.Companion.EXTRA_DATE_TIME
import com.axel_stein.glucose_tracker.ui.edit_a1c.EditA1cActivity.Companion.EXTRA_ID

@Suppress("UNCHECKED_CAST")
class EditA1cFactory(private val id: Long, private val state: Bundle?): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditA1cViewModel::class.java)) {
            return if (state == null) {
                EditA1cViewModel(id) as T
            } else {
                val id = state.getLong(EXTRA_ID)
                val a1c = state.getString(EXTRA_A1C, "")
                val dateTime = state.getString(EXTRA_DATE_TIME)
                EditA1cViewModel(id, false, a1c, dateTime) as T
            }
        } else {
            throw IllegalArgumentException()
        }
    }
}