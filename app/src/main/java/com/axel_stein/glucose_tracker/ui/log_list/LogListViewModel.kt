package com.axel_stein.glucose_tracker.ui.log_list

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.model.LogItem
import com.axel_stein.glucose_tracker.data.room.dao.LogDao
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.App
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@SuppressLint("CheckResult")
class LogListViewModel: ViewModel() {
    private val items = MutableLiveData<List<LogItem>>()
    private var yearMonth = ""
    private val disposables = CompositeDisposable()

    @Inject
    lateinit var dao: LogDao

    @Inject
    lateinit var appSettings: AppSettings

    @Inject
    lateinit var appResources: AppResources

    init {
        App.appComponent.inject(this)
    }

    fun loadRecentItems() {
        if (items.value.isNullOrEmpty()) {
            disposables.clear()
            disposables.add(dao.getRecentItems().subscribe {
                items.postValue(sort(it.toMutableList()))
            })
        }
    }

    fun loadA1cList() {
        if (items.value.isNullOrEmpty()) {
            disposables.clear()
            disposables.add(dao.getA1cList().subscribe {
                items.postValue(sort(it.toMutableList()))
            })
        }
    }

    fun loadItemsByYearMonth(yearMonth: String) {
        if (yearMonth != this.yearMonth) {
            disposables.clear()
            this.yearMonth = yearMonth
            disposables.add(dao.getItems(yearMonth).subscribe {
                items.postValue(sort(it.toMutableList()))
            })
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
        disposables.dispose()
    }

    fun itemsLiveData(): LiveData<List<LogItem>> {
        return items
    }

    private fun sort(items: MutableList<LogItem>?): List<LogItem>? {
        val useMmol = appSettings.useMmolAsGlucoseUnits()
        items?.forEach {
            it.useMmol = useMmol
            it.valueMmol = "${it.valueMmol} ${appResources.mmolSuffix}"
            it.valueMg = "${it.valueMg} ${appResources.mgSuffix}"
            it.a1c = "${it.a1c}%"
        }
        items?.sortByDescending { it.dateTime.toLocalDate() }
        items?.sortWith(object : Comparator<LogItem> {
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