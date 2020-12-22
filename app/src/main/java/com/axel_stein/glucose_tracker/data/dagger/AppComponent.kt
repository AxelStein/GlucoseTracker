package com.axel_stein.glucose_tracker.data.dagger

import com.axel_stein.glucose_tracker.data.backup.BackupHelper
import com.axel_stein.glucose_tracker.ui.edit_glucose.EditGlucoseActivity
import com.axel_stein.glucose_tracker.ui.edit_glucose.EditGlucoseViewModel
import com.axel_stein.glucose_tracker.ui.edit_note.EditNoteViewModel
import com.axel_stein.glucose_tracker.ui.log_list.LogListViewModel
import com.axel_stein.glucose_tracker.ui.settings.SettingsFragment
import com.axel_stein.glucose_tracker.ui.statistics.StatisticsViewModel
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    fun inject(vm: LogListViewModel)
    fun inject(vm: EditGlucoseViewModel)
    fun inject(vm: EditNoteViewModel)
    fun inject(helper: BackupHelper)
    fun inject(activity: EditGlucoseActivity)
    fun inject(fragment: SettingsFragment)
    fun inject(vm: StatisticsViewModel)
}