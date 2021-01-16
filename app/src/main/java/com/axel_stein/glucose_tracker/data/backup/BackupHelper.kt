package com.axel_stein.glucose_tracker.data.backup

import com.axel_stein.glucose_tracker.data.room.dao.A1cLogDao
import com.axel_stein.glucose_tracker.data.room.dao.GlucoseLogDao
import com.axel_stein.glucose_tracker.data.room.dao.NoteLogDao
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.App
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
    lateinit var a1cLogDao: A1cLogDao

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var appResources: AppResources

    @Inject
    lateinit var appSettings: AppSettings

    val backupFileName = "backup.json"

    init {
        App.appComponent.inject(this)
    }

    fun createBackup(): Single<File> {
        return Single.fromCallable {
            _createBackup()
        }.subscribeOn(io())
    }

    fun _createBackup(): File {
        val backup = Backup(
            1,
            glucoseLogDao.getAll(),
            noteLogDao.get(),
            a1cLogDao.getAll(),
            appSettings.getGlucoseUnits()
        )
        val data = gson.toJson(backup, Backup::class.java)
        val backupFile = File(appResources.appDir(), backupFileName)
        backupFile.writeText(data)
        return backupFile
    }

    fun importBackup(data: String): Completable {
        return Completable.fromAction {
            val backup = gson.fromJson(data, Backup::class.java)
            glucoseLogDao.importBackup(backup.glucoseLogs)
            noteLogDao.importBackup(backup.noteLogs)
            a1cLogDao.importBackup(backup.a1cLogs)
            appSettings.setGlucoseUnits(backup.glucoseUnits)
        }.subscribeOn(io())
    }
}