package com.example.glucose_tracker.ui.log_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.example.glucose_tracker.data.model.LogItem
import com.example.glucose_tracker.data.room.dao.LogDao
import com.example.glucose_tracker.data.settings.AppResources
import com.example.glucose_tracker.data.settings.AppSettings
import com.example.glucose_tracker.ui.App
import javax.inject.Inject

class LogListViewModel: ViewModel() {
    private var items: LiveData<PagedList<LogItem>>? = null

    @Inject
    lateinit var dao: LogDao

    @Inject
    lateinit var appSettings: AppSettings

    @Inject
    lateinit var appResources: AppResources

    init {
        App.appComponent.inject(this)
    }

    fun getItems(): LiveData<PagedList<LogItem>> {
        if (items == null) {
            items = dao.getItems().mapByPage(::map).toLiveData(50)
        }
        return items as LiveData<PagedList<LogItem>>
    }

    private fun map(items: MutableList<LogItem>): List<LogItem> {
        val useMmol = appSettings.useMmolAsGlucoseUnits()
        items.forEach {
            it.useMmol = useMmol
            it.valueMmol = "${it.valueMmol} ${appResources.mmolSuffix}"
            it.valueMg = "${it.valueMg} ${appResources.mgSuffix}"
        }
        items.sortByDescending { it.dateTime.toLocalDate() }
        items.sortWith(object : Comparator<LogItem> {
            override fun compare(a: LogItem?, b: LogItem?): Int {
                val d1 = a?.dateTime?.toLocalDate()
                val d2 = b?.dateTime?.toLocalDate()

                val compareDates = d1?.compareTo(d2)
                if (compareDates == 0) {
                    val t1 = a.dateTime.toLocalTime()
                    val t2 = b?.dateTime?.toLocalTime()
                    return t1.compareTo(t2)
                }
                return 0
            }
        })
        return items
    }
}