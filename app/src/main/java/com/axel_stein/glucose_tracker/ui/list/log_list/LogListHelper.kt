package com.axel_stein.glucose_tracker.ui.list.log_list

import com.axel_stein.glucose_tracker.data.room.repository.LogRepository
import com.axel_stein.glucose_tracker.data.room.repository.LogRepository.LogListResult
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.App
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class LogListHelper {
    private lateinit var repository: LogRepository
    private lateinit var settings: AppSettings
    private lateinit var resources: AppResources

    private var yearMonth = ""
    private val disposables = CompositeDisposable()

    init {
        App.appComponent.inject(this)
    }

    @Inject
    fun setRepository(r: LogRepository) {
        repository = r
    }

    @Inject
    fun setSettings(s: AppSettings) {
        settings = s
    }

    @Inject
    fun setResources(r: AppResources) {
        resources = r
    }

    fun loadRecentItems(callback: (result: LogListResult) -> Unit) {
        loadImpl(repository.getRecentLogs(), callback)
    }

    fun loadA1cList(callback: (result: LogListResult) -> Unit) {
        loadImpl(repository.getA1cLogs(), callback)
    }

    fun loadWeightList(callback: (result: LogListResult) -> Unit) {
        loadImpl(repository.getWeightLogs(), callback)
    }

    fun loadItemsByYearMonth(yearMonth: String, callback: (result: LogListResult) -> Unit) {
        if (yearMonth != this.yearMonth) {
            this.yearMonth = yearMonth
            loadImpl(repository.getLogsByYearMonth(yearMonth), callback)
        }
    }

    private fun loadImpl(observable: Flowable<LogListResult>, callback: (result: LogListResult) -> Unit) {
        disposables.add(
            observable.subscribe({ result ->
                callback(result)
            }, {
                it.printStackTrace()
            })
        )
    }

    fun clear() {
        disposables.clear()
        disposables.dispose()
    }
}