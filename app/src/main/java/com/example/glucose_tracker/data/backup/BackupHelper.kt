package com.example.glucose_tracker.data.backup

import com.example.glucose_tracker.data.room.dao.GlucoseLogDao
import com.example.glucose_tracker.data.room.dao.NoteLogDao
import com.example.glucose_tracker.ui.App
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class BackupHelper {
    @Inject
    lateinit var glucoseLogDao: GlucoseLogDao

    @Inject
    lateinit var noteLogDao: NoteLogDao

    @Inject
    lateinit var gson: Gson

    init {
        App.appComponent.inject(this)
    }

    fun createBackup(): Single<String> {
        return Single.fromCallable {
            val backup = Backup(
                    1,
                    glucoseLogDao.get(),
                    noteLogDao.get()
            )
            gson.toJson(backup, Backup::class.java)
        }.subscribeOn(io())
    }

    fun importBackup(src: String): Completable {
        return Completable.fromAction {
            val backup = gson.fromJson(src, Backup::class.java)
            glucoseLogDao.importBackup(backup.glucoseLogs)
            noteLogDao.importBackup(backup.noteLogs)
        }.subscribeOn(io())
    }
}