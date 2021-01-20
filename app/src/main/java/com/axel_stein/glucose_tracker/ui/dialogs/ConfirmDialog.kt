package com.axel_stein.glucose_tracker.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment


class ConfirmDialog : AppCompatDialogFragment() {
    companion object {
        private const val BUNDLE_TITLE = "BUNDLE_TITLE"
        private const val BUNDLE_MESSAGE = "BUNDLE_MESSAGE"
        private const val BUNDLE_POSITIVE_BTN_TEXT = "BUNDLE_POSITIVE_BTN_TEXT"
        private const val BUNDLE_NEGATIVE_BTN_TEXT = "BUNDLE_NEGATIVE_BTN_TEXT"
    }

    interface OnConfirmListener {
        fun onConfirm(tag: String?)
    }

    @Suppress("unused")
    class Builder {
        private var fragment: Fragment? = null
        private var activity: AppCompatActivity? = null
        private var tag = ""
        private var _title = ""
        private var _message = ""
        private var _positiveBtnText = ""
        private var _negativeBtnText = ""

        fun from(fragment: Fragment, tag: String = "ConfirmDialog"): Builder {
            this.fragment = fragment
            this.tag = tag
            return this
        }

        fun from(activity: AppCompatActivity, tag: String = "ConfirmDialog"): Builder {
            this.activity = activity
            this.tag = tag
            return this
        }

        fun title(id: Int): Builder {
            _title = if (activity != null) {
                activity?.getString(id) ?: ""
            } else {
                fragment?.getString(id) ?: ""
            }
            return this
        }

        fun title(s: String): Builder {
            _title = s
            return this
        }

        fun message(id: Int): Builder {
            _message = if (activity != null) {
                activity?.getString(id) ?: ""
            } else {
                fragment?.getString(id) ?: ""
            }
            return this
        }

        fun message(s: String): Builder {
            _message = s
            return this
        }

        fun positiveBtnText(id: Int): Builder {
            _positiveBtnText = if (activity != null) {
                activity?.getString(id) ?: ""
            } else {
                fragment?.getString(id) ?: ""
            }
            return this
        }

        fun positiveBtnText(s: String): Builder {
            _positiveBtnText = s
            return this
        }

        fun negativeBtnText(id: Int): Builder {
            _negativeBtnText = if (activity != null) {
                activity?.getString(id) ?: ""
            } else {
                fragment?.getString(id) ?: ""
            }
            return this
        }

        fun negativeBtnText(s: String): Builder {
            _negativeBtnText = s
            return this
        }

        fun build(): ConfirmDialog {
            return ConfirmDialog().apply {
                setTargetFragment(fragment, 0)
                title = _title
                message = _message
                positiveBtnText = _positiveBtnText
                negativeBtnText = _negativeBtnText
            }
        }

        fun show() {
            val dialog = build()
            val fm = when {
                fragment != null -> fragment!!.parentFragmentManager
                activity != null -> activity!!.supportFragmentManager
                else -> throw IllegalArgumentException()
            }
            dialog.show(fm, tag)
        }
    }

    private var title = ""
    private var message = ""
    private var positiveBtnText = ""
    private var negativeBtnText = ""
    private var callback: OnConfirmListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        var a: Any? = targetFragment
        if (a == null) {
            a = activity
        }
        setCallback(a)
    }

    private fun setCallback(a: Any?) {
        if (a is OnConfirmListener) {
            callback = a
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        savedInstanceState?.apply {
            title = getString(BUNDLE_TITLE) ?: ""
            message = getString(BUNDLE_MESSAGE) ?: ""
            positiveBtnText = getString(BUNDLE_POSITIVE_BTN_TEXT) ?: ""
            negativeBtnText = getString(BUNDLE_NEGATIVE_BTN_TEXT) ?: ""
        }

        return AlertDialog.Builder(requireContext()).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(positiveBtnText) { _, _ -> run {
                callback?.onConfirm(tag)
                dismiss()
            }}
            setNegativeButton(negativeBtnText) { _, _ -> dismiss()}
        }.create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(BUNDLE_TITLE, title)
        outState.putString(BUNDLE_MESSAGE, message)
        outState.putString(BUNDLE_POSITIVE_BTN_TEXT, positiveBtnText)
        outState.putString(BUNDLE_NEGATIVE_BTN_TEXT, negativeBtnText)
    }
}