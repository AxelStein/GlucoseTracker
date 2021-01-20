package com.axel_stein.glucose_tracker.utils.ui

import android.view.View
import android.widget.AdapterView


fun setItemSelectedListener(callback: (position: Int) -> Unit): AdapterView.OnItemSelectedListener {
    return object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            callback(position)
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}