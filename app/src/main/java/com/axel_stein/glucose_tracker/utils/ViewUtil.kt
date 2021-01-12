package com.axel_stein.glucose_tracker.utils

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import androidx.core.widget.doAfterTextChanged
import com.axel_stein.glucose_tracker.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

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

fun TextInputLayout.showEmptyFieldError(editText: TextInputEditText, error: Boolean) {
    if (error) {
        this.error = context.getString(R.string.no_value)
        editText.showKeyboard()
    }
    this.isErrorEnabled = error
}

fun TextInputEditText.setupEditor(onTextChanged: (String) -> Unit) {
    doAfterTextChanged { onTextChanged(text.toString()) }
    setOnEditorActionListener { _, actionId, _ ->
        var consumed = false
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard()
            consumed = true
        }
        consumed
    }
    setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus) hideKeyboard()
    }
}

fun TextInputEditText.setEditorText(text: String) {
    val current = this.text.toString()
    if (current != text) {
        setText(text)
        setSelection(length())
    }
    if (text.isBlank()) {
        showKeyboard()
    } else if (!isFocused) {
        hideKeyboard()
    }
}

fun AutoCompleteTextView.setupSpinner(inputLayout: TextInputLayout, onItemClick: (position: Int) -> Unit) {
    inputType = 0  // disable ime input
    setOnKeyListener { _, _, _ -> true }  // disable hardware keyboard input
    setOnItemClickListener { _, _, position, _ ->
        inputLayout.clearFocus()
        onItemClick(position)
    }
    setOnDismissListener { inputLayout.clearFocus() }
}

fun AutoCompleteTextView.setSpinnerItems(items: List<String>) {
    setAdapter(
        CArrayAdapter(context, R.layout.item_popup, items.toTypedArray())
    )
}

fun AutoCompleteTextView.setSpinnerItems(items: Array<String>) {
    setAdapter(
        CArrayAdapter(context, R.layout.item_popup, items)
    )
}

fun AutoCompleteTextView.setSpinnerSelection(position: Int) {
    if (position != -1 && position != listSelection) {
        listSelection = position
        val item = adapter?.getItem(position)
        if (item != null) {
            setText(item as String, false)
        }
    }
}