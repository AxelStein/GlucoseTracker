package com.axel_stein.glucose_tracker.data.dagger

import com.axel_stein.glucose_tracker.data.backup.BackupHelper
import com.axel_stein.glucose_tracker.ui.MainActivity
import com.axel_stein.glucose_tracker.ui.archive.ArchiveFragment
import com.axel_stein.glucose_tracker.ui.archive.ArchiveViewModel
import com.axel_stein.glucose_tracker.ui.edit_a1c.EditA1cViewModel
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
    fun inject(vm: EditA1cViewModel)
    fun inject(vm: ArchiveViewModel)
    fun inject(fragment: ArchiveFragment)
    fun inject(activity: MainActivity)
}