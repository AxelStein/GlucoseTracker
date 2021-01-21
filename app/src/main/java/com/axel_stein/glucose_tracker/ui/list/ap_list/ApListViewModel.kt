package com.axel_stein.glucose_tracker.ui.list.ap_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.room.repository.LogRepository.LogListResult
import com.axel_stein.glucose_tracker.ui.list.log_list.LogListHelper

class ApListViewModel : ViewModel() {
    private val logList = MutableLiveData<LogListResult>()
    val logListLiveData = logList as LiveData<LogListResult>

    private val helper = LogListHelper().apply {
        loadApList {
            logList.postValue(it)
        }
    }

    override fun onCleared() {
        super.onCleared()
        helper.clear()
    }
}