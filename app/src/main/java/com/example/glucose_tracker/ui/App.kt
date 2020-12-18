package com.example.glucose_tracker.ui

import android.app.Application
import com.example.glucose_tracker.R
import com.example.glucose_tracker.data.dagger.AppComponent
import com.example.glucose_tracker.data.dagger.AppModule
import com.example.glucose_tracker.data.dagger.DaggerAppComponent

class App: Application() {
    companion object {
        lateinit var appComponent: AppComponent
        lateinit var mmol_l: String
    }

    override fun onCreate() {
        super.onCreate()
        mmol_l = getString(R.string.mmol_l)
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}