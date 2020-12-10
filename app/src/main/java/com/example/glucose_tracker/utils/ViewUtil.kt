package com.example.glucose_tracker.utils

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

fun setViewVisible(visible: Boolean, vararg views: View) {
    for (v in views) {
        v.visibility = when(visible) {
            true -> VISIBLE
            else -> GONE
        }
    }
}

fun showView(vararg views: View) {
    for (v in views) {
        v.visibility = VISIBLE
    }
}

fun hideView(vararg views: View) {
    for (v in views) {
        v.visibility = GONE
    }
}

fun View.setShown(shown: Boolean) {
    visibility = if (shown) VISIBLE else GONE
}

fun View.show() {
    visibility = VISIBLE
}

fun View.hide() {
    visibility = GONE
}