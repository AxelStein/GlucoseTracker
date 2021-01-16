package com.axel_stein.glucose_tracker.ui.edit_insulin

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.model.Insulin
import com.axel_stein.glucose_tracker.data.room.dao.InsulinDao
import com.axel_stein.glucose_tracker.utils.getOrDefault
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers.io

open class EditInsulinViewModelImpl(protected val id: Long = 0L) : ViewModel() {
    protected var title = MutableLiveData<String>()
    protected var type = MutableLiveData<Int>()
    protected var active = MutableLiveData<Boolean>()
    protected var errorEmptyTitle = MutableLiveData<Boolean>()
    protected var actionFinish = MutableLiveData<Boolean>()
    protected lateinit var dao: InsulinDao

    fun titleLiveData(): LiveData<String> = title
    fun typeLiveData(): LiveData<Int> = type
    fun activeLiveData(): LiveData<Boolean> = active
    fun errorEmptyTitleLiveData(): LiveData<Boolean> = errorEmptyTitle
    fun actionFinishLiveData(): LiveData<Boolean> = actionFinish

    @SuppressLint("CheckResult")
    fun loadData() {
        if (id == 0L) postData()
        else dao.getById(id)
            .subscribeOn(io())
            .subscribe({
                postData(it.title, it.type, it.active)
            }, {
                it.printStackTrace()
            })
    }

    private fun postData(title: String = "", type: Int = 0, active: Boolean = true) {
        this.title.postValue(title)
        this.type.postValue(type)
        this.active.postValue(active)
    }

    fun setTitle(title: String) {
        this.title.value = title
        if (title.isNotBlank()) {
            errorEmptyTitle.value = false
        }
    }

    fun setType(type: Int) {
        this.type.value = type
    }

    @SuppressLint("CheckResult")
    fun toggleActive() {
        val updatedValue = !active.getOrDefault(true)
        dao.setActive(id, updatedValue)
            .subscribeOn(io())
            .subscribe({
                active.postValue(updatedValue)
                actionFinish.postValue(true)
            }, {
                it.printStackTrace()
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
                actionFinish.postValue(true)
            }, {
                it.printStackTrace()
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
                { actionFinish.postValue(true) },
                { it.printStackTrace() }
            )
        }
    }
}