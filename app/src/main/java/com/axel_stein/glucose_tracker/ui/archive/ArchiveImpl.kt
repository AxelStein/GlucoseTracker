package com.axel_stein.glucose_tracker.ui.archive

import androidx.lifecycle.SavedStateHandle
import com.axel_stein.glucose_tracker.data.room.repository.LogRepository
import com.axel_stein.glucose_tracker.ui.App
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class ArchiveImpl {
    private lateinit var repository: LogRepository
    private var years = listOf<String>()
    private var selectedYear = -1
    private var months = listOf<Int>()
    private var selectedMonth = -1
    private val disposables = CompositeDisposable()

    var onUpdateYearsListener: ((List<String>) -> Unit?)? = null
    var onUpdateSelectedYearListener: ((Int) -> Unit?)? = null
    var onUpdateMonthsListener: ((List<Int>) -> Unit?)? = null
    var onUpdateSelectedMonthListener: ((Int) -> Unit?)? = null
    var onUpdateYearMonthListener: ((String) -> Unit?)? = null

    init {
        App.appComponent.inject(this)
    }

    @Inject
    fun setRepository(r: LogRepository) {
        repository = r
        disposables.add(loadYears())
    }

    fun setCurrentYear(year: Int) {
        selectedYear = year
        onUpdateSelectedYearListener?.invoke(selectedYear)
        if (years.isNotEmpty()) {
            disposables.add(loadMonths(years[year]))
        } else {
            years = emptyList()
            months = emptyList()
            selectedMonth = -1
        }
    }

    fun setCurrentMonth(month: Int) {
        selectedMonth = month
        onUpdateSelectedMonthListener?.invoke(selectedMonth)
        if (months.isNotEmpty()) {
            loadLogList()
        }
    }

    fun clear() {
        disposables.clear()
        disposables.dispose()
    }

    private fun loadYears(): Disposable {
        return repository.getYears()
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe({ newList ->
                updateYears(newList)
            }, {
                it.printStackTrace()
            })
    }

    private fun updateYears(newList: List<String>) {
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
        years = newList
        onUpdateYearsListener?.invoke(years)
        setCurrentYear(index)
    }

    private fun loadMonths(year: String): Disposable {
        return repository.getMonths(year)
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe({ newList ->
                updateMonths(newList)
            }, {
                it.printStackTrace()
            })
    }

    private fun updateMonths(newList: List<String>) {
        val filteredMonths = newList.map { it.toInt() }
        var index = selectedMonth
        if (months.isNotEmpty()) {  // update months
            if (newList.isNotEmpty()) {
                val currentMonth = getCurrentMonth()
                index = if (filteredMonths.contains(currentMonth)) {
                    filteredMonths.indexOf(currentMonth)
                } else {
                    0
                }
            }
        } else if (newList.isNotEmpty()) {  // load months
            index = 0
        }
        months = filteredMonths
        onUpdateMonthsListener?.invoke(months)
        setCurrentMonth(index)
    }

    private fun getCurrentMonth(): Int = if (months.isEmpty()) -1 else months[selectedMonth]

    private fun loadLogList() {
        if (!years.isNullOrEmpty() && !months.isNullOrEmpty()) {
            val currentYear = years[selectedYear]
            val currentMonth = months[selectedMonth]
            val yearMonth = "$currentYear-${currentMonth.toString().padStart(2, '0')}"
            onUpdateYearMonthListener?.invoke(yearMonth)
        }
    }

    fun restore(state: SavedStateHandle) {
        years = state.get<List<String>>("years") ?: emptyList()
        selectedYear = state.get<Int>("selected_year") ?: -1
        months = state.get<List<Int>>("months") ?: emptyList()
        selectedMonth = state.get<Int>("selected_month") ?: -1
        loadLogList()
    }
}