package com.example.glucose_tracker.data.settings

import com.example.glucose_tracker.R
import com.example.glucose_tracker.ui.App
import java.io.File

class AppResources(val app: App) {
    val mmolSuffix = app.getString(R.string.mmol_l)
    val mgSuffix = app.getString(R.string.mg_dl)

    fun appDir(): File {
        return app.filesDir
    }
}