package com.axel_stein.glucose_tracker.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.axel_stein.glucose_tracker.ui.statistics.helpers.ChartColors

@Suppress("UNCHECKED_CAST")
class StatisticsFactory(private val chartColors: ChartColors) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            return StatisticsViewModel(chartColors = chartColors) as T
        } else {
            throw IllegalArgumentException()
        }
    }

}