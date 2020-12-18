package com.example.glucose_tracker.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.EditText


fun EditText.showKeyboard() {
    post(KeyboardRunnable(this))
}

fun EditText.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.hideSoftInputFromWindow(windowToken, 0)

    clearFocus()
}

fun hide(activity: Activity) {
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.hideSoftInputFromWindow(activity.findViewById<View>(android.R.id.content).windowToken, 0)
}

private class KeyboardRunnable(private val view: View) : Runnable {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    val tag = "KeyboardRunnable"

    override fun run() {
        if (!view.isFocusable && !view.isFocusableInTouchMode) {
            Log.e(tag, "Non focusable view")
        } else if (!view.requestFocus()) {
            Log.e(tag, "Cannot focus on view")
            post()
        } else if (imm != null && !imm.isActive(view)) {
            Log.e(tag, "IMM is not active")
            post()
        } else if (imm != null && !imm.showSoftInput(view, SHOW_IMPLICIT)) {
            Log.e(tag, "Unable to show keyboard")
            post()
        }
    }

    private fun post() {
        view.postDelayed(this, 100)
    }
}
