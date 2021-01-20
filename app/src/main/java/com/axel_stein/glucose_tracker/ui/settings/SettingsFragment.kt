package com.axel_stein.glucose_tracker.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog.OnConfirmListener
import com.axel_stein.glucose_tracker.ui.settings.SettingsViewModel.Companion.CODE_PICK_FILE
import com.axel_stein.glucose_tracker.utils.formatDateTime
import com.axel_stein.glucose_tracker.utils.ui.ProgressListener
import com.google.android.material.snackbar.Snackbar
import org.joda.time.DateTime
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat(), OnConfirmListener {
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

        val height = preferenceManager.findPreference<Preference>("height")
        height?.setOnPreferenceChangeListener { preference, newValue ->
            if (newValue.toString().isBlank() || newValue == "0") {
                preference.summary = resources.getString(R.string.main_pref_height_summary)
            } else {
                preference.summary = "$newValue ${resources.getString(R.string.height_unit_cm)}"
            }
            true
        }
        val h = appSettings.getHeight()
        if (h.isNotEmpty() && h != "0") {
            height?.summary = "$h ${resources.getString(R.string.height_unit_cm)}"
        }

        val exportBackup = preferenceManager.findPreference<Preference>("export_backup")
        exportBackup?.setOnPreferenceClickListener {
            viewModel.startExportToFile()
                .subscribe({
                    startActivity(Intent.createChooser(it, null))
                }, {
                    it.printStackTrace()
                    showMessage(R.string.error_export_file)
                })
            true
        }

        val importBackup = preferenceManager.findPreference<Preference>("import_backup")
        importBackup?.setOnPreferenceClickListener {
            ConfirmDialog.Builder()
                .from(this, "import_backup")
                .title(R.string.dialog_title_confirm)
                .message(R.string.message_import_backup)
                .positiveBtnText(R.string.action_import)
                .negativeBtnText(R.string.action_cancel)
                .show()
            true
        }

        val driveCreateBackup = preferenceManager.findPreference<Preference>("drive_create_backup")
        driveCreateBackup?.setOnPreferenceClickListener {
            ConfirmDialog.Builder()
                .from(this, "drive_create_backup")
                .title(R.string.dialog_title_confirm)
                .message(R.string.message_drive_export)
                .positiveBtnText(R.string.action_create)
                .negativeBtnText(R.string.action_cancel)
                .show()
            true
        }

        val driveImport = preferenceManager.findPreference<Preference>("drive_import")
        driveImport?.setOnPreferenceClickListener {
            ConfirmDialog.Builder()
                .from(this, "drive_import")
                .title(R.string.dialog_title_confirm)
                .message(R.string.message_import_backup)
                .positiveBtnText(R.string.action_import)
                .negativeBtnText(R.string.action_cancel)
                .show()
            true
        }

        lastSynced = preferenceManager.findPreference("drive_last_synced")

        autoSync = preferenceManager.findPreference("drive_auto_sync")
        autoSync?.setOnPreferenceChangeListener { _, enable ->
            viewModel.enableAutoSync(enable as Boolean)
            true
        }

        viewModel.showAutoSyncLiveData.observe(viewLifecycleOwner, {
            autoSync?.isVisible = it
        })

        viewModel.lastSyncTimeLiveData.observe(viewLifecycleOwner, { time ->
            if (time > 0) {
                lastSynced?.summary = formatDateTime(requireContext(), DateTime(time), false)
                lastSynced?.isVisible = true
            }
        })

        viewModel.messageLiveData.observe(viewLifecycleOwner, {
            val msg = it.getContent()
            if (msg != null) {
                showMessage(msg)
            }
        })

        viewModel.showProgressBarLiveData.observe(viewLifecycleOwner, {
            if (activity is ProgressListener) {
                (activity as ProgressListener).showProgress(it)
            }
        })
    }

    private fun showMessage(message: Int) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.onActivityResult(requestCode, resultCode, data)
    }

    override fun onConfirm(tag: String?) {
        when (tag) {
            "drive_import" -> viewModel.driveImportBackup(this)
            "drive_create_backup" -> viewModel.driveCreateBackup(this)
            "import_backup" -> startActivityForResult(viewModel.startImportFromFile(), CODE_PICK_FILE)
        }
    }
}