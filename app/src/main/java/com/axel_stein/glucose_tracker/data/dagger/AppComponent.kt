package com.axel_stein.glucose_tracker.data.dagger

import com.axel_stein.glucose_tracker.data.backup.BackupHelper
import com.axel_stein.glucose_tracker.ui.MainActivity
import com.axel_stein.glucose_tracker.ui.archive.ArchiveFragment
import com.axel_stein.glucose_tracker.ui.archive.ArchiveImpl
import com.axel_stein.glucose_tracker.ui.archive.ArchiveViewModel
import com.axel_stein.glucose_tracker.ui.edit_a1c.EditA1cViewModel
import com.axel_stein.glucose_tracker.ui.edit_glucose.EditGlucoseActivity
import com.axel_stein.glucose_tracker.ui.edit_glucose.EditGlucoseViewModel
import com.axel_stein.glucose_tracker.ui.edit_insulin.EditInsulinViewModel
import com.axel_stein.glucose_tracker.ui.edit_insulin_log.EditInsulinLogViewModel
import com.axel_stein.glucose_tracker.ui.edit_medication.EditMedicationViewModel
import com.axel_stein.glucose_tracker.ui.edit_medication_log.EditMedicationLogViewModel
import com.axel_stein.glucose_tracker.ui.edit_note.EditNoteViewModel
import com.axel_stein.glucose_tracker.ui.edit_weight.EditWeightViewModel
import com.axel_stein.glucose_tracker.ui.insulin_list.InsulinListViewModel
import com.axel_stein.glucose_tracker.ui.log_list.LogListHelper
import com.axel_stein.glucose_tracker.ui.medication_list.MedicationListViewModel
import com.axel_stein.glucose_tracker.ui.settings.SettingsFragment
import com.axel_stein.glucose_tracker.ui.statistics.StatisticsViewModel
import com.axel_stein.glucose_tracker.ui.statistics.helpers.ChartData
import com.axel_stein.glucose_tracker.ui.statistics.helpers.DateLabelInflater
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
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
    fun inject(vm: InsulinListViewModel)
    fun inject(vm: EditInsulinViewModel)
    fun inject(vm: EditInsulinLogViewModel)
    fun inject(vm: EditMedicationViewModel)
    fun inject(vm: MedicationListViewModel)
    fun inject(vm: EditMedicationLogViewModel)
    fun inject(vm: EditWeightViewModel)
    fun inject(helper: LogListHelper)
    fun inject(impl: ArchiveImpl)
    fun inject(chartData: ChartData)
    fun inject(dateLabelInflater: DateLabelInflater)
}