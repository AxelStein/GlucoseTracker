package com.axel_stein.glucose_tracker.ui.archive

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.room.dao.LogDao
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.App
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

@SuppressLint("CheckResult")
class ArchiveViewModel: ViewModel() {
    private val yearsData = MutableLiveData<List<String>>()
    private var years = listOf<String>()

    private val selectedYearData = MutableLiveData<Int>()
    private var selectedYear = 0

    private val monthsData = MutableLiveData<List<Int>>()
    private var months = listOf<Int>()

    private val selectedMonthData = MutableLiveData<Int>()
    private var selectedMonth = 0

    private val loadItemsByYearMonth = MutableLiveData<String>()
    private val disposables = CompositeDisposable()

    @Inject
    lateinit var dao: LogDao

    @Inject
    lateinit var appSettings: AppSettings

    @Inject
    lateinit var appResources: AppResources

    init {
        App.appComponent.inject(this)
        disposables.add(dao.getYears().subscribeOn(io()).subscribe { newList ->
            var index = 0
            if (this.years.isNotEmpty()) {
                val currentYear = this.years[selectedYear]
                index = if (newList.contains(currentYear)) {
                    newList.indexOf(currentYear)
                } else {
                    0
                }
            }
            setYears(newList)
            setCurrentYear(index)
        })
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
        disposables.dispose()
    }

    fun monthsData(): LiveData<List<Int>> {
        return monthsData
    }

    private fun setMonths(list: List<Int>) {
        months = list
        monthsData.postValue(list)
    }

    fun yearsData(): LiveData<List<String>> {
        return yearsData
    }

    private fun setYears(list: List<String>) {
        years = list
        yearsData.postValue(list)
    }

    fun selectedMonthData(): LiveData<Int> {
        return selectedMonthData
    }

    private fun setSelectedMonth(position: Int) {
        selectedMonth = position
        selectedMonthData.postValue(position)
    }

    fun selectedYearData(): LiveData<Int> {
        return selectedYearData
    }

    private fun setSelectedYear(position: Int) {
        selectedYear = position
        selectedYearData.postValue(position)
    }

    fun loadItemsByYearMonthData(): LiveData<String> {
        return loadItemsByYearMonth
    }

    fun setCurrentMonth(position: Int) {
        if (months.isNotEmpty()) {
            setSelectedMonth(position)
            loadItems()
        }
    }

    fun setCurrentYear(position: Int) {
        if (years.isNotEmpty()) {
            setSelectedYear(position)
            loadMonths(years[position])
        } else {
            setYears(emptyList())
            setMonths(emptyList())
        }
    }

    private fun loadMonths(year: String) {
        disposables.add(dao.getMonths(year).subscribeOn(io()).subscribe { newList ->
            val months = mutableListOf<Int>()
            newList.forEach { months.add(it.toInt()) }

            var index = 0
            if (this.months.isNotEmpty()) {
                val currentMonth = this.months[selectedMonth]
                index = if (months.contains(currentMonth)) {
                    months.indexOf(currentMonth)
                } else {
                    0
                }
            }
            setMonths(months)
            setSelectedMonth(index)
            loadItems()
        })
    }

    private fun loadItems() {
        if (!years.isNullOrEmpty() && !months.isNullOrEmpty()) {
            val currentYear = years[selectedYear]
            val currentMonth = months[selectedMonth]
            loadItemsByYearMonth.postValue(
                "$currentYear-${currentMonth.toString().padStart(2, '0')}"
            )
        }
    }
}