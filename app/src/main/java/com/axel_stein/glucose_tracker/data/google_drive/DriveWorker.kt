package com.axel_stein.glucose_tracker.data.google_drive

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.axel_stein.glucose_tracker.data.backup.BackupHelper
import com.axel_stein.glucose_tracker.data.backup.BackupHelper.Companion.BACKUP_FILE_NAME

class DriveWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    private val driveService = GoogleDriveService(context)
    private val backupHelper = BackupHelper()

    override fun doWork(): Result {
        return try {
            val backupFile = backupHelper.createBackupImpl()
            driveService.uploadFileImpl(BACKUP_FILE_NAME, backupFile)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}