package com.axel_stein.glucose_tracker.data.room

import androidx.room.TypeConverter
import org.joda.time.DateTime

class Converters {
    @TypeConverter
    fun dateToStr(date: DateTime): String = date.toString()

    @TypeConverter
    fun strToDate(s: String): DateTime = DateTime(s)
}