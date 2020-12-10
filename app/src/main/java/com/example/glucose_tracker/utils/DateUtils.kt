package com.example.glucose_tracker.utils

import android.content.Context
import android.text.format.DateUtils.*
import org.joda.time.LocalDate


fun formatDate(context: Context, date: LocalDate, showWeekDay: Boolean=true): String {
    var flags = FORMAT_SHOW_DATE
    if (showWeekDay) {
        flags = flags or (FORMAT_SHOW_WEEKDAY or FORMAT_ABBREV_WEEKDAY)
    }
    return formatDateTime(context, date.toDateTimeAtCurrentTime().millis, flags)
}