package com.axel_stein.glucose_tracker.utils.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatSpinner

class CSpinner : AppCompatSpinner {
    constructor(context: Context) : super(context)
    constructor(context: Context, mode: Int) : super(context, mode)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int, mode: Int) : super(context, attrs, defStyle, mode)

    private var userActionOnSpinner = true

    fun setItemSelectedListener(callback: (pos: Int, userAction: Boolean) -> Unit) {
        onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                callback(position, userActionOnSpinner)
                userActionOnSpinner = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    fun setUserAction(isUserAction: Boolean) {
        userActionOnSpinner = isUserAction
    }

    fun setSelectionProgrammatically(position: Int) {
        if (position != selectedItemPosition) {
            userActionOnSpinner = false
            setSelection(position)
        }
    }
}