package com.axel_stein.glucose_tracker.utils

import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.get() = value!!

fun <T> MutableLiveData<T>.getOrDefault(default: T) = value ?: default

fun MutableLiveData<String>.notBlankOrDefault(default: String): String {
    return if (value.isNullOrBlank()) default else value ?: ""
}