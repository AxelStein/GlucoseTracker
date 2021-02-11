package com.axel_stein.glucose_tracker.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.axel_stein.glucose_tracker.data.room.dao.*
import com.axel_stein.glucose_tracker.data.room.model.*

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
    ],
    version = 2
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
}