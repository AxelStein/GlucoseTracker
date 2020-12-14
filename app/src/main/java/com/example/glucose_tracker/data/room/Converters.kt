package com.example.glucose_tracker.data.room

import androidx.room.TypeConverter
import org.joda.time.DateTime

class Converters {
    @TypeConverter
    fun dateToStr(date: DateTime): String {
        return date.toString()
    }

    @TypeConverter
    fun strToDate(s: String): DateTime {
        return DateTime(s)
    }
}