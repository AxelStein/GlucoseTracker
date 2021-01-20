package com.axel_stein.glucose_tracker.data.backup

import com.axel_stein.glucose_tracker.data.room.dao.*
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
    lateinit var medicationDao: MedicationDao

    @Inject
    lateinit var medicationLogDao: MedicationLogDao

    @Inject
    lateinit var insulinDao: InsulinDao

    @Inject
    lateinit var insulinLogDao: InsulinLogDao

    @Inject
    lateinit var weightLogDao: WeightLogDao

    @Inject
    lateinit var apLogDao: ApLogDao

    @Inject
    lateinit var pulseLogDao: PulseLogDao

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var appResources: AppResources

    @Inject
    lateinit var appSettings: AppSettings

    companion object {
        const val BACKUP_FILE_NAME = "backup.json"
    }

    init {
        App.appComponent.inject(this)
    }

    fun createBackup(): Single<File> {
        return Single.fromCallable {
            createBackupImpl()
        }.subscribeOn(io())
    }

    fun createBackupImpl(): File {
        val backup = Backup(
            3,
            glucoseLogDao.getAll(),
            noteLogDao.get(),
            a1cLogDao.getAll(),
            appSettings.getGlucoseUnits(),
            appSettings.getHeight(),
            medicationDao.getAll(),
            medicationLogDao.getAll(),
            insulinDao.getAll(),
            insulinLogDao.getAll(),
            weightLogDao.getAll(),
            apLogDao.getAll(),
            pulseLogDao.getAll(),
        )
        val data = gson.toJson(backup, Backup::class.java)
        val backupFile = File(appResources.appDir(), BACKUP_FILE_NAME)
        backupFile.writeText(data)
        return backupFile
    }

    fun importBackup(data: String): Completable {
        return Completable.fromAction {
            val backup = gson.fromJson(data, Backup::class.java)
            // version 1
            glucoseLogDao.importBackup(backup.glucoseLogs)
            noteLogDao.importBackup(backup.noteLogs)
            a1cLogDao.importBackup(backup.a1cLogs)
            appSettings.setGlucoseUnits(backup.glucoseUnits)

            if (backup.version >= 2) {
                appSettings.setHeight(backup.height)
                medicationDao.importBackup(backup.medications)
                medicationLogDao.importBackup(backup.medicationLogs)
                insulinDao.importBackup(backup.insulinList)
                insulinLogDao.importBackup(backup.insulinLogs)
                weightLogDao.importBackup(backup.weightLogs)

                if (backup.version == 3) {
                    apLogDao.importBackup(backup.apLogs)
                    pulseLogDao.importBackup(backup.pulseLogs)
                }
            }
        }.subscribeOn(io())
    }
}