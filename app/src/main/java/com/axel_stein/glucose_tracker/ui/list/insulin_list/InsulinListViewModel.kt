package com.axel_stein.glucose_tracker.ui.list.insulin_list

import android.app.Application
import android.util.SparseArray
import androidx.core.util.set
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.room.model.Insulin
import com.axel_stein.glucose_tracker.data.room.dao.InsulinDao
import com.axel_stein.glucose_tracker.ui.App
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class InsulinListViewModel(app: Application) : AndroidViewModel(app) {
    private val items = MutableLiveData<InsulinListResult>()
    private val disposables = CompositeDisposable()
    private lateinit var dao: InsulinDao

    init {
        App.appComponent.inject(this)
        loadData()
    }

    @Inject
    fun setDao(dao: InsulinDao) {
        this.dao = dao
    }

    fun itemsLiveData(): LiveData<InsulinListResult> = items

    fun loadData() {
        disposables.add(
            dao.observeItems().subscribeOn(io()).subscribe(
                {
                    val list = it.sortedByDescending { item -> item.active }
                    items.postValue(InsulinListResult(list, createHeaders(list)))
                },
                { it.printStackTrace() }
            )
        )
    }

    private fun createHeaders(list: List<Insulin>): SparseArray<String> {
        val headers = SparseArray<String>()
        var active: Boolean? = null
        list.forEachIndexed { index, item ->
            val itemActive = item.active
            if (active == null || active != itemActive) {
                headers[index] = getApplication<App>().getString(if (itemActive) {
                    R.string.hint_active_insulin_list
                } else {
                    R.string.hint_suspended_insulin_list
                })
                active = itemActive
            }
        }
        return headers
    }

    data class InsulinListResult(
        val list: List<Insulin>,
        val headers: SparseArray<String>
    )

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
        disposables.dispose()
    }
}