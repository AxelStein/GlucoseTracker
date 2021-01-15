package com.axel_stein.glucose_tracker.ui.medication_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.model.Medication
import com.axel_stein.glucose_tracker.data.room.dao.MedicationDao
import com.axel_stein.glucose_tracker.ui.App
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MedicationListViewModel : ViewModel() {
    private val items = MutableLiveData<List<Medication>>()
    private val disposables = CompositeDisposable()

    @Inject
    lateinit var dao: MedicationDao

    init {
        App.appComponent.inject(this)
        loadData()
    }

    fun itemsLiveData(): LiveData<List<Medication>> = items

    fun loadData() {
        disposables.add(
            dao.observeItems().subscribeOn(Schedulers.io()).subscribe(
                {
                    items.postValue(it)
                },
                { it.printStackTrace() }
            )
        )
    }

    // private fun sort(items: List<Medication>) = items.sortedByDescending { it.active }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
        disposables.dispose()
    }
}