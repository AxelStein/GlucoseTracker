package com.axel_stein.glucose_tracker.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.room.LogRepository.LogListResult
import com.axel_stein.glucose_tracker.ui.log_list.LogListHelper

class HomeViewModel : ViewModel() {
    private val logList = MutableLiveData<LogListResult>()
    val logListLiveData = logList as LiveData<LogListResult>

    private val helper = LogListHelper().apply {
        loadRecentItems {
            logList.postValue(it)
        }
    }

    override fun onCleared() {
        super.onCleared()
        helper.clear()
    }
}