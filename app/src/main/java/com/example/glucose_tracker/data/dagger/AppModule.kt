package com.example.glucose_tracker.data.dagger

import androidx.room.Room
import com.example.glucose_tracker.data.room.AppDatabase
import com.example.glucose_tracker.data.room.dao.*
import com.example.glucose_tracker.ui.App
import com.google.gson.*
import dagger.Module
import dagger.Provides
import org.joda.time.DateTime
import java.lang.reflect.Type
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
}