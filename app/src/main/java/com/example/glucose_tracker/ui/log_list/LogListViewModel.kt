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
        items = dao.getItems().toLiveData(50)
    }

    /*
    private fun formatItems(items: List<LogItem>): List<LogItem> {
        items.sortedWith(object : Comparator<LogItem> {
            override fun compare(a: LogItem?, b: LogItem?): Int {
                val d1 = LocalDate(a?.date)
                val d2 = LocalDate(b?.date)
                val c = d2.compareTo(d1)
                if (c == 0) {
                    val t1 = LocalTime(a?.date)
                    val t2 = LocalTime(a?.date)
                    return t1.compareTo(t2)
                }
                return c
            }
        })
        return items.sortedWith(compareByDescending { it.date })
    }
    */
    /*
    private fun formatItem(item: LogItem): LogItem {
        item.valueMmol = item.valueMmol + " mmol/L"
        return item
    }
    */
}