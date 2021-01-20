package com.axel_stein.glucose_tracker.ui

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.axel_stein.glucose_tracker.utils.DateTimeProvider
import com.axel_stein.glucose_tracker.utils.formatDate
import com.axel_stein.glucose_tracker.utils.formatTime
import com.axel_stein.glucose_tracker.utils.getOrDateTime
import com.axel_stein.glucose_tracker.utils.ui.showDatePicker
import com.axel_stein.glucose_tracker.utils.ui.showTimePicker
import org.joda.time.MutableDateTime

open class EditorActivity : AppCompatActivity() {
    protected lateinit var date: TextView
    protected lateinit var time: TextView
    private lateinit var dateTimeProvider: DateTimeProvider

    protected fun setupToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { finish() }
    }

    protected fun setupDateTime(date: TextView, time: TextView, provider: DateTimeProvider) {
        this.dateTimeProvider = provider
        provider.dateTimeLiveData().observe(this, { setDateTime(it) })

        this.date = date
        date.setOnClickListener {
            val dateTime = provider.dateTimeLiveData().getOrDateTime()
            showDatePicker(this, dateTime) { year, month, dayOfMonth ->
                provider.onDateSet(year, month, dayOfMonth)
            }
        }

        this.time = time
        time.setOnClickListener {
            val dateTime = provider.dateTimeLiveData().getOrDateTime()
            showTimePicker(this, dateTime) { hourOfDay, minuteOfHour ->
                provider.onTimeSet(hourOfDay, minuteOfHour)
            }
        }
    }

    private fun setDateTime(dateTime: MutableDateTime) {
        date.text = formatDate(this, dateTime)
        time.text = formatTime(this, dateTime)
    }
}