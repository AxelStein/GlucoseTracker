package com.axel_stein.glucose_tracker.ui.edit.edit_insulin

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.google_drive.DriveWorkerScheduler
import com.axel_stein.glucose_tracker.data.room.dao.InsulinDao
import com.axel_stein.glucose_tracker.data.room.model.Insulin
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.utils.getOrDefault
import com.axel_stein.glucose_tracker.utils.ui.Event
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class EditInsulinViewModel(private val id: Long = 0L, private val state: SavedStateHandle) : ViewModel() {
    private val title = MutableLiveData<String>()
    val titleLiveData: LiveData<String> = title

    private val type = MutableLiveData<Int>()
    val typeLiveData: LiveData<Int> = type

    private val active = MutableLiveData<Boolean>()
    val activeLiveData: LiveData<Boolean> = active

    private val errorEmptyTitle = MutableLiveData<Boolean>()
    val errorEmptyTitleLiveData: LiveData<Boolean> = errorEmptyTitle

    private val actionFinish = MutableLiveData<Event<Boolean>>()
    val actionFinishLiveData: LiveData<Event<Boolean>> = actionFinish

    private val showMessage = MutableLiveData<Event<Int>>()
    val showMessageLiveData: LiveData<Event<Int>> = showMessage

    private lateinit var dao: InsulinDao
    private lateinit var scheduler: DriveWorkerScheduler

    init {
        App.appComponent.inject(this)
        loadData()
    }

    @Inject
    fun inject(dao: InsulinDao, scheduler: DriveWorkerScheduler) {
        this.dao = dao
        this.scheduler = scheduler
    }

    @SuppressLint("CheckResult")
    fun loadData() {
        if (id == 0L) setData()
        else dao.getById(id)
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe({
                setData(it.title, it.type, it.active)
            }, {
                it.printStackTrace()
            })
    }

    private fun setData(title: String = "", type: Int = 0, active: Boolean = true) {
        this.title.value = state.get("title") ?: title
        this.type.value = state.get("type") ?: type
        this.active.value = active
    }

    fun setTitle(title: String) {
        this.title.value = title
        state["title"] = title
        if (title.isNotBlank()) {
            errorEmptyTitle.value = false
        }
    }

    fun setType(type: Int) {
        this.type.value = type
        state["type"] = type
    }

    @SuppressLint("CheckResult")
    fun toggleActive() {
        val updatedValue = !active.getOrDefault(true)
        dao.setActive(id, updatedValue)
            .subscribeOn(io())
            .subscribe({
                scheduler.schedule()
                active.postValue(updatedValue)
                actionFinish.postValue(Event())
            }, {
                it.printStackTrace()
                showMessage.postValue(Event(R.string.error_toggle_active))
            })
    }

    @SuppressLint("CheckResult")
    fun save() {
        if (title.value.isNullOrBlank()) {
            errorEmptyTitle.value = true
        } else {
            Completable.fromAction {
                dao.upsert(createLog())
            }.subscribeOn(io()).subscribe({
                scheduler.schedule()
                actionFinish.postValue(Event())
            }, {
                it.printStackTrace()
                showMessage.postValue(Event(R.string.error_saving))
            })
        }
    }

    private fun createLog() =
        Insulin(
            title.value ?: "",
            type.value ?: 0
        ).also { it.id = id }

    @SuppressLint("CheckResult")
    fun delete() {
        if (id != 0L) {
            dao.deleteById(id).subscribeOn(io()).subscribe(
                {
                    scheduler.schedule()
                    actionFinish.postValue(Event())
                },
                {
                    it.printStackTrace()
                    showMessage.postValue(Event(R.string.error_deleting))
                }
            )
        }
    }
}