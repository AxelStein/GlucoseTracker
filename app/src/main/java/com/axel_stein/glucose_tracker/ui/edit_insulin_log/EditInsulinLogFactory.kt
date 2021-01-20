package com.axel_stein.glucose_tracker.ui.edit_insulin_log

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner


@Suppress("UNCHECKED_CAST")
class EditInsulinLogFactory(
        owner: SavedStateRegistryOwner,
        private val id: Long
): AbstractSavedStateViewModelFactory(owner, null) {
    override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
    ): T = EditInsulinLogViewModel(id, handle) as T
}