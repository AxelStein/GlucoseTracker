package com.axel_stein.glucose_tracker.ui.statistics.helpers

import org.joda.time.DateTime

class DateLabelInflater(size: Int, private val months: Array<String>) {
    private val labels = Array(size) { "" }
    private var currentMonth = -1
    private var currentDay = -1
    private var forceShowDay = false

    fun add(index: Int, dateTime: DateTime) {
        val month = dateTime.monthOfYear-1
        val day = dateTime.dayOfMonth
        if (currentMonth != month) {
            currentMonth = month
            currentDay = day
            labels[index] = months[month]
            forceShowDay = true
            // labels[index] = "${months[month]} $day"
        } else {
            if (forceShowDay || currentDay != day) {
                forceShowDay = false
                currentDay = day
                labels[index] = dateTime.dayOfMonth.toString()
            } else {
                labels[index] = ""
            }
        }
    }

    fun labels() = labels
}