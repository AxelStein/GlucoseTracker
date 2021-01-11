package com.axel_stein.glucose_tracker.ui.insulin_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.model.Insulin
import com.axel_stein.glucose_tracker.data.room.dao.InsulinDao
import javax.inject.Inject

class InsulinListViewModel : ViewModel() {
    private val items = MutableLiveData<List<Insulin>>()
    @Inject
    lateinit var dao: InsulinDao

    init {
        loadData()
    }

    fun itemsLiveData(): LiveData<List<Insulin>> = items

    fun loadData() {
        // todo
        items.postValue(
            listOf(Insulin("Actrapid", 1), Insulin("Humalog"), Insulin("Lantus", 2))
        )
    }
}