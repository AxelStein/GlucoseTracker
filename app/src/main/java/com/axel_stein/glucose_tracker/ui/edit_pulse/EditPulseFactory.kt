package com.axel_stein.glucose_tracker.ui.edit_pulse

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner

@Suppress("UNCHECKED_CAST")
class EditPulseFactory(
    owner: SavedStateRegistryOwner,
    private val id: Long
) : AbstractSavedStateViewModelFactory(owner, null) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T = EditPulseViewModel(id, handle) as T
}