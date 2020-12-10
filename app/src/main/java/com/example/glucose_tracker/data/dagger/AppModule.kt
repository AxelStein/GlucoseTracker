package com.example.glucose_tracker.data.dagger

import androidx.room.Room
import com.example.glucose_tracker.data.room.*
import com.example.glucose_tracker.ui.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: App) {
    @Provides
    @Singleton
    fun provideDB(): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, app.packageName).build()
    }

    @Provides
    @Singleton
    fun provideFoodListDao(db: AppDatabase): FoodListDao {
        return db.foodListDao()
    }

    @Provides
    @Singleton
    fun provideFoodLogDao(db: AppDatabase): FoodLogDao {
        return db.foodLogDao()
    }

    @Provides
    @Singleton
    fun provideGlucoseLogDao(db: AppDatabase): GlucoseLogDao {
        return db.glucoseLogDao()
    }

    @Provides
    @Singleton
    fun provideNoteLogDao(db: AppDatabase): NoteLogDao {
        return db.noteLogDao()
    }

    @Provides
    @Singleton
    fun provideLogDao(db: AppDatabase): LogDao {
        return db.logDao()
    }
}