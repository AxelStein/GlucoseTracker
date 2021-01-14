package com.axel_stein.glucose_tracker.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.lifecycle.ViewModelProvider
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.utils.ui.ProgressListener
import com.axel_stein.glucose_tracker.utils.formatDateTime
import org.joda.time.DateTime
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var viewModel: SettingsViewModel
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
        setPreferencesFromResource(R.xml.main_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val theme = preferenceManager.findPreference<ListPreference>("theme")
        theme?.setOnPreferenceChangeListener { _, value ->
            appSettings.applyTheme(value as String)
            true
        }

        val exportBackup = preferenceManager.findPreference<Preference>("export_backup")
        exportBackup?.setOnPreferenceClickListener {
            viewModel.startExportToFile()
                .subscribe({
                    startActivity(Intent.createChooser(it, null))
                }, {
                    it.printStackTrace()
                    showSnackbar(R.string.error_export_file)
                })
            true
        }

        val importBackup = preferenceManager.findPreference<Preference>("import_backup")
        importBackup?.setOnPreferenceClickListener {
            startActivityForResult(viewModel.startImportFromFile(), viewModel.codePickFile)
            true
        }

        val driveCreateBackup = preferenceManager.findPreference<Preference>("drive_create_backup")
        driveCreateBackup?.setOnPreferenceClickListener {
            viewModel.driveCreateBackup(this)
            true
        }

        val driveImport = preferenceManager.findPreference<Preference>("drive_import")
        driveImport?.setOnPreferenceClickListener {
            viewModel.driveImportBackup(this)
            true
        }

        lastSynced = preferenceManager.findPreference("drive_last_synced")

        autoSync = preferenceManager.findPreference("drive_auto_sync")
        autoSync?.setOnPreferenceChangeListener { _, enable ->
            viewModel.enableAutoSync(enable as Boolean)
            true
        }

        viewModel.showAutoSyncLiveData().observe(viewLifecycleOwner, {
            autoSync?.isVisible = it
        })

        viewModel.lastSyncTimeLiveData().observe(viewLifecycleOwner, { time ->
            if (time > 0) {
                lastSynced?.summary = formatDateTime(requireContext(), DateTime(time), false)
                lastSynced?.isVisible = true
            }
        })

        viewModel.messageLiveData().observe(viewLifecycleOwner, { message ->
            showSnackbar(message)
        })

        viewModel.showProgressBarLiveData().observe(viewLifecycleOwner, {
            if (activity is ProgressListener) {
                (activity as ProgressListener).showProgress(it)
            }
        })
    }

    private fun showSnackbar(message: Int) {
        if (message != -1) {
            val c = context
            if (c != null) {
                Toast.makeText(c, message, LENGTH_SHORT).show()
            }
            viewModel.notifyMessageReceived()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.onActivityResult(requestCode, resultCode, data)
    }
}