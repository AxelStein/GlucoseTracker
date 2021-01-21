package com.axel_stein.glucose_tracker.ui.list.medication_list

import android.app.Application
import android.util.SparseArray
import androidx.core.util.set
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.room.model.Medication
import com.axel_stein.glucose_tracker.data.room.dao.MedicationDao
import com.axel_stein.glucose_tracker.ui.App
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class MedicationListViewModel(app: Application) : AndroidViewModel(app) {
    private val items = MutableLiveData<MedicationListResult>()
    private val disposables = CompositeDisposable()
    private lateinit var dao: MedicationDao

    init {
        App.appComponent.inject(this)
        loadData()
    }

    @Inject
    fun setDao(dao: MedicationDao) {
        this.dao = dao
    }

    fun itemsLiveData(): LiveData<MedicationListResult> = items

    fun loadData() {
        disposables.add(
            dao.observeItems().subscribeOn(io()).subscribe(
                { items ->
                    val list = items.sortedByDescending { item -> item.active }
                    this.items.postValue(MedicationListResult(list, createHeaders(list)))
                },
                { it.printStackTrace() }
            )
        )
    }

    private fun createHeaders(list: List<Medication>): SparseArray<String> {
        val headers = SparseArray<String>()
        var active: Boolean? = null
        list.forEachIndexed { index, item ->
            val itemActive = item.active
            if (active == null || active != itemActive) {
                headers[index] = getApplication<App>().getString(if (itemActive) {
                    R.string.hint_active_medications
                } else {
                    R.string.hint_suspended_medications
                })
                active = itemActive
            }
        }
        return headers
    }

    data class MedicationListResult(
        val list: List<Medication>,
        val headers: SparseArray<String>
    )

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
        disposables.dispose()
    }
}