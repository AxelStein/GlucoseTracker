package com.axel_stein.glucose_tracker.ui.settings

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.utils.formatDateTime
import org.joda.time.DateTime
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var viewModel: SettingsViewModel
    private val codePickFile = 1
    private val codeRequestPermissions = 2

    private var lastSynced: Preference? = null
    private var autoSync: SwitchPreference? = null

    @Inject
    lateinit var appSettings: AppSettings

    init {
        App.appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = requireContext().applicationContext as App
        viewModel = ViewModelProvider(this, SettingsFactory(app))
            .get(SettingsViewModel::class.java)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nightMode = preferenceManager.findPreference<SwitchPreference>("night_mode")
        nightMode?.setOnPreferenceChangeListener { _, mode ->
            appSettings.enableNightMode(mode as Boolean)
            true
        }

        val exportBackup = preferenceManager.findPreference<Preference>("export_backup")
        exportBackup?.setOnPreferenceClickListener {
            viewModel.startExportToFile()
                .subscribe({
                    startActivity(Intent.createChooser(it, null))
                }, {
                    it.printStackTrace()
                    Toast.makeText(requireContext(), R.string.error_export_file, LENGTH_SHORT).show()
                })
            true
        }

        val importBackup = preferenceManager.findPreference<Preference>("import_backup")
        importBackup?.setOnPreferenceClickListener {
            startActivityForResult(viewModel.startImportFromFile(), codePickFile)
            true
        }

        val driveCreateBackup = preferenceManager.findPreference<Preference>("drive_create_backup")
        driveCreateBackup?.setOnPreferenceClickListener {
            driveCreateBackup()
        }

        val driveImport = preferenceManager.findPreference<Preference>("drive_import")
        driveImport?.setOnPreferenceClickListener {
            driveImportBackup()
        }

        lastSynced = preferenceManager.findPreference("drive_last_synced")
        updateLastSyncTime()

        autoSync = preferenceManager.findPreference("drive_auto_sync")
        autoSync?.isVisible = viewModel.hasPermissions()
        autoSync?.setOnPreferenceChangeListener { _, enable ->
            viewModel.enableAutoSync(enable as Boolean)
            true
        }
    }

    @SuppressLint("CheckResult")
    private fun driveCreateBackup(): Boolean {
        if (viewModel.requestPermissions(this, codeRequestPermissions, "create")) {
            viewModel.driveCreateBackup()
                .doOnComplete { updateLastSyncTime() }
                .subscribe({
                    Toast.makeText(requireContext(), R.string.msg_backup_created, LENGTH_SHORT).show()
                }, {
                    it.printStackTrace()
                    Toast.makeText(requireContext(), R.string.error_create_backup, LENGTH_SHORT).show()
                })
        }
        return true
    }

    @SuppressLint("CheckResult")
    private fun driveImportBackup(): Boolean {
        if (viewModel.requestPermissions(this, codeRequestPermissions, "import")) {
            viewModel.driveImportBackup()
                .subscribe({
                    Toast.makeText(requireContext(), R.string.msg_import_completed, LENGTH_SHORT).show()
                }, {
                    it.printStackTrace()
                    Toast.makeText(requireContext(), R.string.error_import_backup, LENGTH_SHORT).show()
                })
        }
        return true
    }

    @SuppressLint("CheckResult")
    private fun updateLastSyncTime() {
        viewModel.getLastSyncTime()
            .subscribe({ time ->
                if (time != -1L) {
                    lastSynced?.summary = formatDateTime(requireContext(), DateTime(time), false)
                    lastSynced?.isVisible = true
                }
            }, {
                it.printStackTrace()
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                codePickFile -> {
                    viewModel.importFromFile(data?.data)
                        .subscribe({
                            Toast.makeText(requireContext(), R.string.msg_import_completed, LENGTH_SHORT).show()
                        }, {
                            it.printStackTrace()
                            Toast.makeText(requireContext(), R.string.error_import_file, LENGTH_SHORT).show()
                        })
                }

                codeRequestPermissions -> {
                    autoSync?.isVisible = true
                    updateLastSyncTime()

                    when (viewModel.lastAction) {
                        "create" -> driveCreateBackup()
                        "import" -> driveImportBackup()
                    }
                }
            }
        }
    }
}