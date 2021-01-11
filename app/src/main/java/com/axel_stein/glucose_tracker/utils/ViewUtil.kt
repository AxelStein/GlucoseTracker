package com.axel_stein.glucose_tracker.utils

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup

fun setViewVisible(visible: Boolean, vararg views: View?) {
    for (v in views) {
        v?.visibility = when(visible) {
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

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}