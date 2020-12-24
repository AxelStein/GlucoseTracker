package com.axel_stein.glucose_tracker.utils

import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils.*
import org.joda.time.DateTime
import org.joda.time.MutableDateTime


fun formatDate(context: Context, dateTime: MutableDateTime?, showWeekDay: Boolean = true): String {
    return formatDate(context, dateTime?.toDateTime(), showWeekDay)
}

fun formatDate(context: Context, dateTime: DateTime?, showWeekDay: Boolean = true): String {
    if (dateTime == null) return ""
    var flags = FORMAT_SHOW_DATE
    if (showWeekDay) {
        flags = flags or (FORMAT_SHOW_WEEKDAY or FORMAT_ABBREV_WEEKDAY)
    }
    return formatDateTime(context, dateTime.millis, flags)
}

fun formatTime(context: Context, dateTime: MutableDateTime?): String {
    return formatTime(context, dateTime?.toDateTime())
}

fun formatTime(context: Context, dateTime: DateTime?): String {
    if (dateTime == null) return ""
    val flags = FORMAT_SHOW_TIME or FORMAT_ABBREV_TIME
    return formatDateTime(context, dateTime.millis, flags)
}

fun is24HourFormat(context: Context): Boolean {
    return DateFormat.is24HourFormat(context)
}