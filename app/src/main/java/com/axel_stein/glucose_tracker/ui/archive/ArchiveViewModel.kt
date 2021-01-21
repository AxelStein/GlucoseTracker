package com.axel_stein.glucose_tracker.ui.archive

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.room.repository.LogRepository.LogListResult
import com.axel_stein.glucose_tracker.ui.list.log_list.LogListHelper

@SuppressLint("CheckResult")
class ArchiveViewModel(state: SavedStateHandle): ViewModel() {
    private val years = MutableLiveData<List<String>>()
    val yearsLiveData = years as LiveData<List<String>>

    private val selectedYear = MutableLiveData<Int>()
    val selectedYearLiveData = selectedYear as LiveData<Int>

    private val months = MutableLiveData<List<Int>>()
    val monthsLiveData = months as LiveData<List<Int>>

    private val selectedMonth = MutableLiveData<Int>()
    val selectedMonthLiveData = selectedMonth as LiveData<Int>

    private val logList = MutableLiveData<LogListResult>()
    val logListLiveData = logList as LiveData<LogListResult>

    private val helper = LogListHelper()
    private val impl = ArchiveImpl().apply {
        onUpdateYearsListener = {
            years.setValue(it)
            state.set("years", it)
        }
        onUpdateSelectedYearListener = {
            selectedYear.setValue(it)
            state.set("selected_year", it)
        }
        onUpdateMonthsListener = {
            months.setValue(it)
            state.set("months", it)
        }
        onUpdateSelectedMonthListener = {
            selectedMonth.setValue(it)
            state.set("selected_month", it)
        }
        onUpdateYearMonthListener = { yearMonth ->
            helper.loadItemsByYearMonth(yearMonth) {
                logList.postValue(it)
            }
        }
    }

    init {
        impl.restore(state)
    }

    fun setCurrentYear(position: Int) {
        impl.setCurrentYear(position)
    }

    fun setCurrentMonth(position: Int) {
        impl.setCurrentMonth(position)
    }

    override fun onCleared() {
        super.onCleared()
        helper.clear()
        impl.clear()
    }
}