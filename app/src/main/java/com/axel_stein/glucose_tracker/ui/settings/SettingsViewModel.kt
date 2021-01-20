package com.axel_stein.glucose_tracker.ui.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.backup.BackupHelper
import com.axel_stein.glucose_tracker.data.backup.BackupHelper.Companion.BACKUP_FILE_NAME
import com.axel_stein.glucose_tracker.data.google_drive.DriveWorker
import com.axel_stein.glucose_tracker.data.google_drive.GoogleDriveService
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.utils.readStrFromFileUri
import com.axel_stein.glucose_tracker.utils.ui.Event
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import java.io.File
import java.util.concurrent.TimeUnit

class SettingsViewModel(app: App) : AndroidViewModel(app) {
    companion object {
        const val CODE_PICK_FILE = 1
        private const val CODE_REQUEST_PERMISSIONS = 2
    }

    private val showProgressBar = MutableLiveData(false)
    val showProgressBarLiveData: LiveData<Boolean> = showProgressBar

    private val showAutoSync = MutableLiveData(false)
    val showAutoSyncLiveData: LiveData<Boolean> = showAutoSync

    private val lastSyncTime = MutableLiveData(0L)
    val lastSyncTimeLiveData: LiveData<Long> = lastSyncTime

    private val message = MutableLiveData<Event<Int>>()
    val messageLiveData: LiveData<Event<Int>> = message

    private val driveService = GoogleDriveService(app)
    private val backupHelper = BackupHelper()
    private var lastAction = ""

    init {
        if (driveService.hasPermissions()) {
            showAutoSync.value = true
        }
        updateLastSyncTime()
    }

    fun startExportToFile() = backupHelper.createBackup()
        .map { file ->
            Intent(ACTION_SEND).apply {
                type = "*/*"
                putExtra(EXTRA_STREAM, getUriForFile(file))
                flags = FLAG_GRANT_WRITE_URI_PERMISSION or FLAG_GRANT_READ_URI_PERMISSION
            }
        }
        .observeOn(mainThread())
        .doOnSubscribe { showProgressBar(true) }
        .doFinally { showProgressBar(false) }

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

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            CODE_PICK_FILE -> {
                importFromFile(data?.data)
                    .subscribe({
                        message.value = Event(R.string.dialog_msg_import_completed)
                    }, {
                        it.printStackTrace()
                        message.value = Event(R.string.error_import_file)
                    })
            }

            CODE_REQUEST_PERMISSIONS -> {
                showAutoSync.value = true
                updateLastSyncTime()

                when (lastAction) {
                    "create" -> driveCreateBackup()
                    "import" -> driveImportBackup()
                }
            }
        }
    }

    private fun importFromFile(uri: Uri?) = readStrFromFileUri(getApplication<App>().contentResolver, uri)
        .flatMapCompletable { backup -> backupHelper.importBackup(backup) }
        .observeOn(mainThread())
        .doOnSubscribe { showProgressBar(true) }
        .doFinally { showProgressBar(false) }

    @SuppressLint("CheckResult")
    fun driveCreateBackup(fragment: Fragment) {
        if (!driveService.hasPermissions()) {
            lastAction = "create"
            driveService.requestPermissions(fragment, CODE_REQUEST_PERMISSIONS)
        } else {
            driveCreateBackup()
        }
    }

    @SuppressLint("CheckResult")
    private fun driveCreateBackup() {
        backupHelper.createBackup()
            .flatMapCompletable { file -> driveService.uploadFile(BACKUP_FILE_NAME, file) }
            .observeOn(mainThread())
            .doOnSubscribe { showProgressBar(true) }
            .doOnComplete { updateLastSyncTime() }
            .doFinally { showProgressBar(false) }
            .subscribe({
                message.value = Event(R.string.dialog_msg_backup_created)
            }, {
                it.printStackTrace()
                message.value = Event(R.string.error_create_backup)
            })
    }

    fun driveImportBackup(fragment: Fragment) {
        if (!driveService.hasPermissions()) {
            lastAction = "import"
            driveService.requestPermissions(fragment, CODE_REQUEST_PERMISSIONS)
        } else {
            driveImportBackup()
        }
    }

    @SuppressLint("CheckResult")
    private fun driveImportBackup() {
        driveService.downloadFile(BACKUP_FILE_NAME)
            .flatMapCompletable { data -> backupHelper.importBackup(data) }
            .observeOn(mainThread())
            .doOnSubscribe { showProgressBar(true) }
            .doFinally { showProgressBar(false) }
            .subscribe({
                message.value = Event(R.string.dialog_msg_import_completed)
            }, {
                it.printStackTrace()
                message.value = Event(R.string.error_import_backup)
            })
    }

    @SuppressLint("CheckResult")
    private fun updateLastSyncTime() {
        if (driveService.hasPermissions()) {
            driveService.getLastSyncTime(BACKUP_FILE_NAME)
                .observeOn(mainThread())
                .doOnSubscribe { showProgressBar(true) }
                .doFinally { showProgressBar(false) }
                .subscribe({
                    lastSyncTime.postValue(it)
                }, {
                    it.printStackTrace()
                })
        }
    }

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

    private fun showProgressBar(show: Boolean) {
        showProgressBar.value = show
    }
}