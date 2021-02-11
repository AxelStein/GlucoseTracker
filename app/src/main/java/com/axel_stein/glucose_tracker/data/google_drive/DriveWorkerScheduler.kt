package com.axel_stein.glucose_tracker.data.google_drive

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

class DriveWorkerScheduler(private val context: Context) {

    fun schedule() {
        val tag = "com.axel_stein.drive_worker"
        val wm = WorkManager.getInstance(context)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        Completable.fromAction {
            OneTimeWorkRequestBuilder<DriveWorker>()
                .setConstraints(constraints)
                .addTag(tag)
                .build()
                .also {
                    wm.enqueue(it)
                }
        }.subscribeOn(Schedulers.io()).subscribe()
    }
}