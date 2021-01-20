package com.axel_stein.glucose_tracker.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.axel_stein.glucose_tracker.data.model.*
import com.axel_stein.glucose_tracker.data.room.dao.*

@Database(
    entities = [
        GlucoseLog::class,
        NoteLog::class,
        A1cLog::class,
        Insulin::class,
        InsulinLog::class,
        Medication::class,
        MedicationLog::class,
        WeightLog::class,
        ApLog::class,
        PulseLog::class
    ],
    version = 3
)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun glucoseLogDao(): GlucoseLogDao
    abstract fun noteLogDao(): NoteLogDao
    abstract fun logDao(): LogDao
    abstract fun statsDao(): StatsDao
    abstract fun a1cDao(): A1cLogDao
    abstract fun insulinDao(): InsulinDao
    abstract fun insulinLogDao(): InsulinLogDao
    abstract fun medicationDao(): MedicationDao
    abstract fun medicationLogDao(): MedicationLogDao
    abstract fun weightLogDao(): WeightLogDao
    abstract fun apLogDao(): ApLogDao
    abstract fun pulseLogDao(): PulseLogDao
}