package com.axel_stein.glucose_tracker.utils.ui


@Suppress("unused")
class Event<T>(private val content: T? = null) {
    private var handled = false

    fun getContent(): T? {
        return if (handled) {
            null
        } else {
            handleEvent()
            content
        }
    }

    fun handleEvent() {
        handled = true
    }

    fun isHandled() = handled
}