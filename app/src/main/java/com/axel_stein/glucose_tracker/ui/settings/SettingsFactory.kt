package com.axel_stein.glucose_tracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.axel_stein.glucose_tracker.ui.App

@Suppress("UNCHECKED_CAST")
class SettingsFactory(private val app: App) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(app) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}