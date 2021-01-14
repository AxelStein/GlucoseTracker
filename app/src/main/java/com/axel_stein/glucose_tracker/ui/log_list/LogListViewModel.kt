package com.axel_stein.glucose_tracker.ui.log_list

import android.annotation.SuppressLint
import android.app.Application
import android.util.SparseArray
import androidx.core.util.set
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.axel_stein.glucose_tracker.data.model.LogItem
import com.axel_stein.glucose_tracker.data.room.dao.LogDao
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.utils.formatDate
import com.axel_stein.glucose_tracker.utils.formatTime
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.LocalDate
import javax.inject.Inject

@SuppressLint("CheckResult")
class LogListViewModel(app: Application): AndroidViewModel(app) {
    private val items = MutableLiveData<List<LogItem>>()
    private val headers = MutableLiveData<SparseArray<String>>()
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

    fun itemsLiveData(): LiveData<List<LogItem>> = items

    fun headersLiveData(): LiveData<SparseArray<String>> = headers

    fun loadRecentItems() {
        if (items.value.isNullOrEmpty()) {
            loadImpl(dao.getRecentItems())
        }
    }

    fun loadA1cList() {
        if (items.value.isNullOrEmpty()) {
            loadImpl(dao.getA1cList())
        }
    }

    fun loadItemsByYearMonth(yearMonth: String) {
        if (yearMonth != this.yearMonth) {
            this.yearMonth = yearMonth
            loadImpl(dao.getItems(yearMonth))
        }
    }

    private fun loadImpl(observable: Flowable<List<LogItem>>) {
        disposables.clear()
        disposables.add(
            observable
                .map {
                    val sorted = sortItems(it.toMutableList())
                    FormattedLogList(sorted, createHeaders(sorted))
                }
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribe({
                    items.value = it.items
                    headers.value = it.headers
                }, {
                    it.printStackTrace()  // todo
                })
        )
    }

    private fun sortItems(items: MutableList<LogItem>): List<LogItem> {
        items.forEach {
            it.useMmol = appSettings.useMmolAsGlucoseUnits()
            it.valueMmol = "${it.valueMmol} ${appResources.mmolSuffix}"
            it.valueMg = "${it.valueMg} ${appResources.mgSuffix}"
            it.a1c = "${it.a1c}%"
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

    private fun createHeaders(items: List<LogItem>): SparseArray<String> {
        val headers = SparseArray<String>()
        var date: LocalDate? = null
        items.forEachIndexed { index, item ->
            val itemDate = item.dateTime.toLocalDate()
            if (date == null || date != itemDate) {
                headers[index] = formatDate(getApplication(), item.dateTime)
                date = itemDate
            }
            item.timeFormatted = formatTime(getApplication(), item.dateTime)
        }
        return headers
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
        disposables.dispose()
    }

    private data class FormattedLogList(
        val items: List<LogItem>,
        val headers: SparseArray<String>
    )
}