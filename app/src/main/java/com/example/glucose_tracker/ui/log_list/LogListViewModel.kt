package com.example.glucose_tracker.ui.log_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.example.glucose_tracker.data.model.LogItem
import com.example.glucose_tracker.data.room.LogDao
import com.example.glucose_tracker.ui.App
import javax.inject.Inject

class LogListViewModel: ViewModel() {
    var items: LiveData<PagedList<LogItem>>

    @Inject
    lateinit var dao: LogDao

    init {
        App.appComponent.inject(this)
        items = dao.getItems().map(::formatItem).toLiveData(50)
    }

    private fun formatItem(item: LogItem): LogItem {
        item.valueMmol = item.valueMmol + " mmol/L"
        return item
    }
}