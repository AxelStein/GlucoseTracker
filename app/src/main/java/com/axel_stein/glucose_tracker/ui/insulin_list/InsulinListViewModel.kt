package com.axel_stein.glucose_tracker.ui.insulin_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.model.Insulin
import com.axel_stein.glucose_tracker.data.room.dao.InsulinDao
import com.axel_stein.glucose_tracker.ui.App
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class InsulinListViewModel : ViewModel() {
    private val items = MutableLiveData<List<Insulin>>()
    private val disposables = CompositeDisposable()

    @Inject
    lateinit var dao: InsulinDao

    init {
        App.appComponent.inject(this)
        loadData()
    }

    fun itemsLiveData(): LiveData<List<Insulin>> = items

    fun loadData() {
        disposables.add(
            dao.get().subscribeOn(io()).subscribe(
                { items.postValue(it) },
                { it.printStackTrace() }
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
        disposables.dispose()
    }
}