package com.axel_stein.glucose_tracker.data.google_drive

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.axel_stein.glucose_tracker.data.backup.BackupHelper
import org.joda.time.LocalDate

class DriveWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    private val driveService = GoogleDriveService(context)
    private val backupHelper = BackupHelper()

    override fun doWork(): Result {
        val time = driveService.getLastSyncTimeImpl(backupHelper.backupFileName)
        if (time != -1L) {
            val date = LocalDate(time)
            val today = LocalDate()
            if (date == today) return Result.success()
        }
        return createBackup()
    }

    private fun createBackup(): Result {
        return try {
            val backupFile = backupHelper._createBackup()
            driveService.uploadFileImpl(backupHelper.backupFileName, backupFile)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}