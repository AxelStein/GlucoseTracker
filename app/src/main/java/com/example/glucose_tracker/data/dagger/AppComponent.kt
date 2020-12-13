package com.example.glucose_tracker.data.dagger

import com.example.glucose_tracker.ui.edit_glucose.EditGlucoseViewModel
import com.example.glucose_tracker.ui.log_list.LogListViewModel
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    fun inject(vm: LogListViewModel)
    fun inject(vm: EditGlucoseViewModel)
}