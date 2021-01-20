package com.axel_stein.glucose_tracker.ui.pulse_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.room.LogRepository.LogListResult
import com.axel_stein.glucose_tracker.ui.log_list.LogListHelper

class PulseListViewModel : ViewModel() {
    private val logList = MutableLiveData<LogListResult>()
    val logListLiveData = logList as LiveData<LogListResult>

    private val helper = LogListHelper().apply {
        loadPulseList {
            logList.postValue(it)
        }
    }

    override fun onCleared() {
        super.onCleared()
        helper.clear()
    }
}