package com.axel_stein.glucose_tracker.ui.edit_insulin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.glucose_tracker.data.room.dao.InsulinDao

open class EditInsulinViewModelImpl(protected val id: Long = 0L) : ViewModel() {
    protected var title = MutableLiveData<String>()
    protected var type = MutableLiveData<Int>()
    protected var errorEmptyTitle = MutableLiveData<Boolean>()
    protected var actionFinish = MutableLiveData<Boolean>()
    protected var dao: InsulinDao? = null

    fun titleLiveData(): LiveData<String> = title
    fun typeLiveData(): LiveData<Int> = type
    fun errorEmptyTitleLiveData(): LiveData<Boolean> = errorEmptyTitle
    fun actionFinishLiveData(): LiveData<Boolean> = actionFinish

    fun loadData() {
        title.postValue("")
        type.postValue(0)
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

    fun save() {
        if (title.value.isNullOrBlank()) {
            errorEmptyTitle.value = true
        } else {
            // todo save
            actionFinish.value = true
        }
    }

    fun delete() {
        // todo delete
        actionFinish.value = true
    }
}