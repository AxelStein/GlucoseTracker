package com.axel_stein.glucose_tracker.ui

import android.app.Application
import com.axel_stein.glucose_tracker.data.dagger.AppComponent
import com.axel_stein.glucose_tracker.data.dagger.AppModule
import com.axel_stein.glucose_tracker.data.dagger.DaggerAppComponent

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