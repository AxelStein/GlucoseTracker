package com.axel_stein.glucose_tracker.data.dagger

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.room.AppDatabase
import com.axel_stein.glucose_tracker.data.room.repository.LogRepository
import com.axel_stein.glucose_tracker.data.room.dao.*
import com.axel_stein.glucose_tracker.data.room.migration_1_2
import com.axel_stein.glucose_tracker.data.room.migration_2_3
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.google.gson.*
import dagger.Module
import dagger.Provides
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.DateTime
import java.lang.reflect.Type
import javax.inject.Singleton

@Module
class AppModule(private val ctx: Context) {
    @Provides
    @Singleton
    fun provideDB(): AppDatabase {
        return Room.databaseBuilder(ctx, AppDatabase::class.java, ctx.packageName)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    Completable.fromAction {
                        db.insert("medication_list", CONFLICT_IGNORE, ContentValues().apply {
                            put("title", ctx.getString(R.string.metformin))
                            put("dosage", 500f)
                            put("dosage_unit", 0)
                        })
                        db.insert("insulin_list", CONFLICT_IGNORE, ContentValues().apply {
                            put("title", ctx.getString(R.string.humalog))
                        })
                    }.subscribeOn(io()).subscribe()
                }
            })
            .addMigrations(migration_1_2(), migration_2_3())
            .build()
    }

    @Provides
    @Singleton
    fun provideLogRepository(db: AppDatabase, dao: LogDao): LogRepository {
        return LogRepository(ctx, db, dao)
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

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(
                DateTime::class.java,
                JsonSerializer {
                    src: DateTime, _: Type?, _: JsonSerializationContext? -> JsonPrimitive(src.toString())
                } as JsonSerializer<DateTime>
            )
            .registerTypeAdapter(
                DateTime::class.java,
                JsonDeserializer {
                    json: JsonElement, _: Type?, _: JsonDeserializationContext? -> DateTime(json.asString)
                } as JsonDeserializer<DateTime>
            )
            .create()
    }

    @Provides
    @Singleton
    fun provideAppSettings(): AppSettings {
        return AppSettings(ctx)
    }

    @Provides
    @Singleton
    fun provideAppResources(): AppResources {
        return AppResources(ctx)
    }

    @Provides
    @Singleton
    fun provideStatsDao(db: AppDatabase): StatsDao {
        return db.statsDao()
    }

    @Provides
    @Singleton
    fun provideA1cDao(db: AppDatabase): A1cLogDao {
        return db.a1cDao()
    }

    @Provides
    @Singleton
    fun provideInsulinDao(db: AppDatabase): InsulinDao {
        return db.insulinDao()
    }

    @Provides
    @Singleton
    fun provideInsulinLogDao(db: AppDatabase): InsulinLogDao {
        return db.insulinLogDao()
    }

    @Provides
    @Singleton
    fun provideMedicationDao(db: AppDatabase): MedicationDao {
        return db.medicationDao()
    }

    @Provides
    @Singleton
    fun provideMedicationLogDao(db: AppDatabase): MedicationLogDao {
        return db.medicationLogDao()
    }

    @Provides
    @Singleton
    fun provideWeightLogDao(db: AppDatabase): WeightLogDao {
        return db.weightLogDao()
    }

    @Provides
    @Singleton
    fun provideApLogDao(db: AppDatabase): ApLogDao {
        return db.apLogDao()
    }

    @Provides
    @Singleton
    fun providePulseLogDao(db: AppDatabase): PulseLogDao {
        return db.pulseLogDao()
    }
}