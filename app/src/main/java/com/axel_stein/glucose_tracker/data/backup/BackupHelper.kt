package com.axel_stein.glucose_tracker.data.backup

import com.axel_stein.glucose_tracker.data.room.dao.GlucoseLogDao
import com.axel_stein.glucose_tracker.data.room.dao.NoteLogDao
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.utils.writeStr
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers.io
import java.io.File
import javax.inject.Inject

class BackupHelper {
    @Inject
    lateinit var glucoseLogDao: GlucoseLogDao

    @Inject
    lateinit var noteLogDao: NoteLogDao

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var appResources: AppResources

    @Inject
    lateinit var appSettings: AppSettings

    private val backupFileName = "backup.json"

    init {
        App.appComponent.inject(this)
    }

    fun createBackup(): Single<File> {
        return Single.fromCallable {
            val backup = Backup(
                    1,
                    glucoseLogDao.get(),
                    noteLogDao.get(),
                    appSettings.getGlucoseUnits()
            )
            val data = gson.toJson(backup, Backup::class.java)
            val backupFile = File(appResources.appDir(), backupFileName)
            backupFile.writeStr(data)
        }.subscribeOn(io())
    }

    fun importBackup(data: String): Completable {
        return Completable.fromAction {
            val backup = gson.fromJson(data, Backup::class.java)
            glucoseLogDao.importBackup(backup.glucoseLogs)
            noteLogDao.importBackup(backup.noteLogs)
            appSettings.setGlucoseUnits(backup.glucoseUnits)
        }.subscribeOn(io())
    }
}