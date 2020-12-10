package com.example.glucose_tracker.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.glucose_tracker.data.model.FoodList
import com.example.glucose_tracker.data.model.FoodLog
import com.example.glucose_tracker.data.model.GlucoseLog
import com.example.glucose_tracker.data.model.NoteLog

@Database(entities = [FoodList::class, FoodLog::class, GlucoseLog::class, NoteLog::class],
        version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun foodLogDao(): FoodLogDao
    abstract fun foodListDao(): FoodListDao
    abstract fun glucoseLogDao(): GlucoseLogDao
    abstract fun noteLogDao(): NoteLogDao
    abstract fun logDao(): LogDao
}