package com.axel_stein.glucose_tracker.ui.settings

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.FileProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.backup.BackupHelper
import com.axel_stein.glucose_tracker.data.google_drive.DriveWorker
import com.axel_stein.glucose_tracker.data.google_drive.GoogleDriveService
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.utils.formatDateTime
import com.axel_stein.glucose_tracker.utils.readStrFromFileUri
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import org.joda.time.DateTime
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class SettingsFragment : PreferenceFragmentCompat() {
    private val backupHelper = BackupHelper()
    private val codePickFile = 1
    private val codeRequestPermissions = 2

    private lateinit var driveService: GoogleDriveService
    private var lastSynced: Preference? = null
    private var autoSync: SwitchPreference? = null
    private var driveAction = ""

    @Inject
    lateinit var appSettings: AppSettings

    init {
        App.appComponent.inject(this)
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
            backupHelper.createBackup()
                .observeOn(mainThread())
                .subscribe({ file ->
                    val uri = getUriForFile(file)

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "*/*"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        flags = FLAG_GRANT_WRITE_URI_PERMISSION or
                                FLAG_GRANT_READ_URI_PERMISSION
                    }
                    startActivity(Intent.createChooser(intent, null))
                }, {
                    it.printStackTrace()
                    Toast.makeText(requireContext(), R.string.error_export_file, LENGTH_SHORT).show()
                })
            true
        }

        val importBackup = preferenceManager.findPreference<Preference>("import_backup")
        importBackup?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                flags = FLAG_GRANT_WRITE_URI_PERMISSION or
                        FLAG_GRANT_READ_URI_PERMISSION
            }
            startActivityForResult(intent, codePickFile)
            true
        }

        if (savedInstanceState != null) {
            driveAction = savedInstanceState.getString("drive_action", "")
        }

        driveService = GoogleDriveService(requireContext())

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
        autoSync?.isVisible = driveService.hasPermissions()
        autoSync?.setOnPreferenceChangeListener { _, enable ->
            val tag = "com.axel_stein.drive_worker"
            val wm = WorkManager.getInstance(requireContext())
            if (enable as Boolean) {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
                val request = PeriodicWorkRequestBuilder<DriveWorker>(1, TimeUnit.DAYS)
                    .setConstraints(constraints)
                    .addTag(tag)
                    .build()
                wm.enqueue(request)
            } else {
                wm.cancelAllWorkByTag(tag)
            }
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("drive_action", driveAction)
    }

    private fun driveCreateBackup(): Boolean {
        if (driveService.hasPermissions()) {
            backupHelper.createBackup()
                .flatMapCompletable { file ->
                    driveService.uploadFile(backupHelper.backupFileName, file)
                }
                .observeOn(mainThread())
                .subscribe({
                    Toast.makeText(requireContext(), R.string.msg_backup_created, LENGTH_SHORT).show()
                }, {
                    it.printStackTrace()
                    Toast.makeText(requireContext(), R.string.error_create_backup, LENGTH_SHORT).show()
                })
        } else {
            driveAction = "create"
            driveService.requestPermissions(this, codeRequestPermissions)
        }
        return true
    }

    private fun driveImportBackup(): Boolean {
        if (driveService.hasPermissions()) {
            driveService.downloadFile(backupHelper.backupFileName)
                .flatMapCompletable { data ->
                    backupHelper.importBackup(data)
                }
                .observeOn(mainThread())
                .subscribe({
                    Toast.makeText(requireContext(), R.string.msg_import_completed, LENGTH_SHORT).show()
                }, {
                    it.printStackTrace()
                    Toast.makeText(requireContext(), R.string.error_import_backup, LENGTH_SHORT).show()
                })
        } else {
            driveAction = "import"
            driveService.requestPermissions(this, codeRequestPermissions)
        }
        return true
    }

    private fun updateLastSyncTime() {
        driveService.getLastSyncTime(backupHelper.backupFileName)
            .observeOn(mainThread())
            .subscribe({ time ->
                if (time != -1L) {
                    lastSynced?.summary = formatDateTime(requireContext(), DateTime(time), false)
                    lastSynced?.isVisible = true
                }
            }, {
                it.printStackTrace()
            })
    }

    private fun getUriForFile(file: File): Uri {
        return FileProvider.getUriForFile(
            requireContext(),
            "com.axel_stein.glucose_tracker.fileprovider",
            file
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                codePickFile -> importFromFile(data?.data)
                codeRequestPermissions -> {
                    autoSync?.isVisible = true
                    updateLastSyncTime()

                    when (driveAction) {
                        "create" -> driveCreateBackup()
                        "import" -> driveImportBackup()
                    }
                }
            }
        }
    }

    private fun importFromFile(uri: Uri?) {
        readStrFromFileUri(requireContext().contentResolver, uri)
            .flatMapCompletable { backupHelper.importBackup(it) }
            .observeOn(mainThread())
            .subscribe({
                Toast.makeText(requireContext(), R.string.msg_import_completed, LENGTH_SHORT).show()
            }, {
                it.printStackTrace()
                Toast.makeText(requireContext(), R.string.error_import_file, LENGTH_SHORT).show()
            })
    }
}