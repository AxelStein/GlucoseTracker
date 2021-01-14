package com.axel_stein.glucose_tracker.utils.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import com.axel_stein.glucose_tracker.utils.is24HourFormat
import org.joda.time.DateTime

fun showDatePicker(context: Context, dateTime: DateTime, callback: (year: Int, month: Int, dayOfMonth: Int) -> Unit) {
    val dialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth -> callback(year, month + 1, dayOfMonth) },
        dateTime.year, dateTime.monthOfYear-1, dateTime.dayOfMonth
    )
    // set today limit
    dialog.datePicker.maxDate = DateTime.now().millis
    dialog.show()
}

fun showTimePicker(context: Context, dateTime: DateTime, callback: (hourOfDay: Int, minuteOfHour: Int) -> Unit) {
    TimePickerDialog(
        context,
        { _, hourOfDay, minuteOfHour -> callback(hourOfDay, minuteOfHour) },
        dateTime.hourOfDay, dateTime.minuteOfHour, is24HourFormat(context)
    ).show()
}