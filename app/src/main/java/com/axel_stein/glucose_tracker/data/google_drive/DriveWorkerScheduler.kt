package com.axel_stein.glucose_tracker.data.google_drive

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo.State.ENQUEUED
import androidx.work.WorkManager
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers.io
import java.util.concurrent.TimeUnit

class DriveWorkerScheduler(private val context: Context) {

    fun schedule() {
        val tag = "com.axel_stein.drive_worker"
        val wm = WorkManager.getInstance(context)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        Completable.fromAction {
            val works = wm.getWorkInfosByTag(tag).get()
            var addRequest = true
            works.forEach {
                if (it.state == ENQUEUED) {
                    addRequest = false
                }
            }
            if (addRequest) {
                OneTimeWorkRequestBuilder<DriveWorker>()
                    .setConstraints(constraints)
                    .addTag(tag)
                    .setInitialDelay(6, TimeUnit.HOURS)
                    .build()
                    .also {
                        wm.enqueue(it)
                    }
            }
        }.subscribeOn(io()).subscribe()
    }
}