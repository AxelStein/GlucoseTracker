package com.axel_stein.glucose_tracker.data.dagger

import android.content.Context
import androidx.room.Room
import com.axel_stein.glucose_tracker.data.room.AppDatabase
import com.axel_stein.glucose_tracker.data.room.dao.*
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.google.gson.*
import dagger.Module
import dagger.Provides
import org.joda.time.DateTime
import java.lang.reflect.Type
import javax.inject.Singleton

@Module
class AppModule(private val ctx: Context) {
    @Provides
    @Singleton
    fun provideDB(): AppDatabase {
        return Room.databaseBuilder(ctx, AppDatabase::class.java, ctx.packageName).build()
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
    fun provideAppResources(settings: AppSettings): AppResources {
        return AppResources(ctx, settings)
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
    fun provideInsulinDao(): InsulinDao {
        return InsulinDao()
    }

    @Provides
    fun provideInsulinLogDao(): InsulinLogDao {
        return InsulinLogDao()
    }

    @Provides
    fun provideMedicationDao(): MedicationDao {
        return MedicationDao()
    }
}