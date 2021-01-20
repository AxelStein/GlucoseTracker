package com.axel_stein.glucose_tracker.utils

import androidx.lifecycle.LiveData
import org.joda.time.MutableDateTime

interface DateTimeProvider {
    fun dateTimeLiveData(): LiveData<MutableDateTime>
    fun onDateSet(year: Int, month: Int, dayOfMonth: Int)
    fun onTimeSet(hourOfDay: Int, minuteOfHour: Int)
}