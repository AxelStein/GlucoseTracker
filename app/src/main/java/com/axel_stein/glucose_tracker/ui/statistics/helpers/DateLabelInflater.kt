package com.axel_stein.glucose_tracker.ui.statistics.helpers

import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.ui.App
import com.github.mikephil.charting.data.Entry
import org.joda.time.DateTime
import javax.inject.Inject

class DateLabelInflater {
    private lateinit var entries: ArrayList<Entry>
    private lateinit var labels: Array<String>
    private var currentMonth = -1
    private var currentDay = -1
    private var forceShowDay = false
    private var maxValue = 0f
    private lateinit var months: Array<String>

    init {
        App.appComponent.inject(this)
    }

    @Inject
    fun setMonthsArray(r: AppResources) {
        months = r.monthsAbbrArray
    }

    fun inflate(list: List<Any>, getItem: (Any) -> Pair<Float, DateTime>) {
        entries = ArrayList(list.size)
        labels = Array(list.size) { "" }

        list.forEachIndexed { index, any ->
            val item = getItem(any)
            val value = item.first
            if (value >= maxValue) {
                maxValue = value
            }
            entries.add(Entry(index.toFloat(), value))
            addLabel(index, item.second)
        }
    }

    private fun addLabel(index: Int, dateTime: DateTime) {
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

    fun getEntries() = entries

    fun getLabels() = labels

    fun getMaxValue() = maxValue
}