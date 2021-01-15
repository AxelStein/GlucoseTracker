package com.axel_stein.glucose_tracker.ui.archive

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.room.LogRepository
import com.axel_stein.glucose_tracker.ui.App
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

@SuppressLint("CheckResult")
class ArchiveViewModel(
    repository: LogRepository? = null
): ViewModel() {
    private val yearsData = MutableLiveData<List<String>>()
    private var years = listOf<String>()

    private val selectedYearData = MutableLiveData<Int>()
    private var selectedYear = -1

    private val monthsData = MutableLiveData<List<Int>>()
    private var months = listOf<Int>()

    private val selectedMonthData = MutableLiveData<Int>()
    private var selectedMonth = -1

    private val loadItemsByYearMonth = MutableLiveData<String>()
    private val disposables = CompositeDisposable()

    @Inject
    lateinit var repository: LogRepository

    init {
        if (repository == null) {
            App.appComponent.inject(this)
        } else {
            this.repository = repository
        }

        disposables.add(
            this.repository.getYears()
                .subscribeOn(io())
                .subscribe({ newList ->
                    var index = selectedYear
                    if (this.years.isNotEmpty()) {  // update years
                        if (newList.isEmpty()) {
                            index = -1
                        } else if (index >= newList.size) {
                            index = 0
                        }
                    } else if (newList.isNotEmpty()) {  // load years
                        index = 0
                    }
                    setYears(newList)
                    setCurrentYear(index)
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

    fun monthsLiveData(): LiveData<List<Int>> = monthsData

    private fun setMonths(list: List<Int>) {
        months = list
        monthsData.postValue(list)
    }

    fun yearsLiveData(): LiveData<List<String>> = yearsData

    private fun setYears(list: List<String>) {
        years = list
        yearsData.postValue(list)
    }

    fun selectedMonthLiveData(): LiveData<Int> = selectedMonthData

    private fun setSelectedMonth(position: Int) {
        selectedMonth = position
        selectedMonthData.postValue(position)
    }

    fun getSelectedMonth(): Int = selectedMonth

    fun getCurrentMonth(): Int = if (months.isEmpty()) -1 else months[selectedMonth]

    fun selectedYearLiveData(): LiveData<Int> = selectedYearData

    private fun setSelectedYear(position: Int) {
        selectedYear = position
        selectedYearData.postValue(position)
    }

    fun getSelectedYear(): Int = selectedYear

    fun getCurrentYear(): String = if (years.isEmpty()) "" else years[selectedYear]

    fun loadItemsByYearMonthLiveData(): LiveData<String> = loadItemsByYearMonth

    fun setCurrentMonth(position: Int) {
        setSelectedMonth(position)
        if (months.isNotEmpty()) {
            loadItems()
        }
    }

    fun setCurrentYear(position: Int) {
        setSelectedYear(position)
        if (years.isNotEmpty()) {
            loadMonths(years[position])
        } else {
            setYears(emptyList())

            setMonths(emptyList())
            setSelectedMonth(-1)
        }
    }

    private fun loadMonths(year: String) {
        disposables.add(
            repository.getMonths(year)
                .subscribeOn(io())
                .subscribe({ newList ->
                    val months = mutableListOf<Int>()
                    newList.forEach { months.add(it.toInt()) }

                    var index = selectedMonth
                    if (this.months.isNotEmpty()) {  // update months
                        if (newList.isNotEmpty()) {
                            val currentMonth = getCurrentMonth()
                            index = if (months.contains(currentMonth)) {
                                months.indexOf(currentMonth)
                            } else {
                                0
                            }
                        }
                    } else if (newList.isNotEmpty()) {  // load months
                        index = 0
                    }

                    setMonths(months)
                    setCurrentMonth(index)
                }, {
                    it.printStackTrace()
                })
        )
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