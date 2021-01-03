package com.axel_stein.glucose_tracker.ui.settings

import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.axel_stein.glucose_tracker.data.backup.BackupHelper
import com.axel_stein.glucose_tracker.data.google_drive.DriveWorker
import com.axel_stein.glucose_tracker.data.google_drive.GoogleDriveService
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.utils.readStrFromFileUri
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import java.io.File
import java.util.concurrent.TimeUnit

class SettingsViewModel(app: App) : AndroidViewModel(app) {
    private val driveService = GoogleDriveService(app)
    private val backupHelper = BackupHelper()
    var lastAction = ""

    fun startExportToFile() = backupHelper.createBackup()
        .map { file ->
            Intent(ACTION_SEND).apply {
                type = "*/*"
                putExtra(EXTRA_STREAM, getUriForFile(file))
                flags = FLAG_GRANT_WRITE_URI_PERMISSION or FLAG_GRANT_READ_URI_PERMISSION
            }
        }
        .observeOn(mainThread())

    private fun getUriForFile(file: File) = FileProvider.getUriForFile(
        getApplication(),
        "com.axel_stein.glucose_tracker.fileprovider",
        file
    )

    fun startImportFromFile() =
        Intent(ACTION_GET_CONTENT).apply {
            addCategory(CATEGORY_OPENABLE)
            type = "*/*"
            flags = FLAG_GRANT_WRITE_URI_PERMISSION or FLAG_GRANT_READ_URI_PERMISSION
        }

    fun importFromFile(uri: Uri?) = readStrFromFileUri(getApplication<App>().contentResolver, uri)
        .flatMapCompletable { backup -> backupHelper.importBackup(backup) }
        .observeOn(mainThread())

    fun hasPermissions() = driveService.hasPermissions()

    fun requestPermissions(fragment: Fragment, requestCode: Int, action: String): Boolean {
        if (!driveService.hasPermissions()) {
            lastAction = action
            driveService.requestPermissions(fragment, requestCode)
            return false
        }
        return true
    }

    fun driveCreateBackup() = backupHelper.createBackup()
        .flatMapCompletable { file -> driveService.uploadFile(backupHelper.backupFileName, file) }
        .observeOn(mainThread())

    fun driveImportBackup() = driveService.downloadFile(backupHelper.backupFileName)
        .flatMapCompletable { data -> backupHelper.importBackup(data) }
        .observeOn(mainThread())

    fun getLastSyncTime() = driveService.getLastSyncTime(backupHelper.backupFileName)
        .observeOn(mainThread())

    fun enableAutoSync(enable: Boolean) {
        val tag = "com.axel_stein.drive_worker"
        val wm = WorkManager.getInstance(getApplication())
        if (enable) {
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
    }
}