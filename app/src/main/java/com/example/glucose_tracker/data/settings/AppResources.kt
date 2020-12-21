package com.example.glucose_tracker.data.settings

import com.example.glucose_tracker.R
import com.example.glucose_tracker.ui.App

class AppResources(val app: App) {
    val mmolSuffix = app.getString(R.string.mmol_l)
    val mgSuffix = app.getString(R.string.mg_dl)
}