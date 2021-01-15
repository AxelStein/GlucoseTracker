package com.axel_stein.glucose_tracker.ui.log_list

import android.annotation.SuppressLint
import android.app.Application
import android.util.SparseArray
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.axel_stein.glucose_tracker.data.room.LogRepository
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.ui.log_list.log_items.LogItem
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@SuppressLint("CheckResult")
class LogListViewModel(app: Application): AndroidViewModel(app) {
    private val items = MutableLiveData<List<LogItem>>()
    private val headers = MutableLiveData<SparseArray<String>>()
    private var yearMonth = ""
    private val disposables = CompositeDisposable()

    @Inject
    lateinit var repository: LogRepository

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
            loadImpl(repository.getRecentLogs())
        }
    }

    fun loadA1cList() {
        if (items.value.isNullOrEmpty()) {
            loadImpl(repository.getA1cLogs())
        }
    }

    fun loadWeightList() {
        if (items.value.isNullOrEmpty()) {
            loadImpl(repository.getWeightLogs())
        }
    }

    fun loadItemsByYearMonth(yearMonth: String) {
        if (yearMonth != this.yearMonth) {
            this.yearMonth = yearMonth
            loadImpl(repository.getLogsByYearMonth(yearMonth))
        }
    }

    private fun loadImpl(observable: Flowable<LogRepository.LogListResult>) {
        disposables.add(
            observable.subscribe({ result ->
                items.postValue(result.list)
                headers.postValue(result.headers)
            }, {
                it.printStackTrace()
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
        disposables.dispose()
    }
}