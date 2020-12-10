package com.example.glucose_tracker.data.room

import androidx.room.TypeConverter
import org.joda.time.LocalDate
import org.joda.time.LocalTime

class Converters {
    @TypeConverter
    fun dateToStr(date: LocalDate): String {
        return date.toString()
    }

    @TypeConverter
    fun strToDate(s: String): LocalDate {
        return LocalDate(s)
    }

    @TypeConverter
    fun timeToStr(time: LocalTime): String {
        return time.toString()
    }

    @TypeConverter
    fun strToTime(s: String): LocalTime {
        return LocalTime(s)
    }
}