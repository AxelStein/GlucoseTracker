package com.example.glucose_tracker.ui

import android.app.Application
import com.example.glucose_tracker.data.dagger.AppComponent
import com.example.glucose_tracker.data.dagger.AppModule
import com.example.glucose_tracker.data.dagger.DaggerAppComponent

class App: Application() {
    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}