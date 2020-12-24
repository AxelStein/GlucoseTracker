package com.axel_stein.glucose_tracker.ui.edit_a1c

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.model.LogItem
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog.OnConfirmListener
import com.axel_stein.glucose_tracker.utils.*
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EditA1cActivity: AppCompatActivity(), OnConfirmListener {
    companion object {
        const val EXTRA_ID = "com.axel_stein.glucose_tracker.ui.edit_a1c.EXTRA_ID"
        const val EXTRA_A1C = "com.axel_stein.glucose_tracker.ui.edit_a1c.EXTRA_A1C"
        const val EXTRA_DATE_TIME = "com.axel_stein.glucose_tracker.ui.edit_a1c.EXTRA_DATE_TIME"

        fun launch(context: Context) {
            context.startActivity(Intent(context, EditA1cActivity::class.java))
        }

        fun launch(context: Context, item: LogItem) {
            val intent = Intent(context, EditA1cActivity::class.java)
            intent.putExtra(EXTRA_ID, item.id)
            context.startActivity(intent)
        }
    }

    private lateinit var viewModel: EditA1cViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_a1c)

        val id = intent.getLongExtra(EXTRA_ID, 0L)
        viewModel = ViewModelProvider(this, EditA1cFactory(id, savedInstanceState))
                .get(EditA1cViewModel::class.java)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { finish() }

        val btnDate = findViewById<TextView>(R.id.btn_date)
        btnDate.setOnClickListener {
            val date = viewModel.getCurrentDateTime()
            DatePickerDialog(
                    this,
                    { _, year, month, dayOfMonth ->
                        viewModel.setDate(year, month+1, dayOfMonth)
                    },
                    date.year, date.monthOfYear-1, date.dayOfMonth
            ).show()
        }

        val btnTime = findViewById<TextView>(R.id.btn_time)
        btnTime.setOnClickListener {
            val time = viewModel.getCurrentDateTime()
            TimePickerDialog(this,
                    { _, hourOfDay, minuteOfHour ->
                        viewModel.setTime(hourOfDay, minuteOfHour)
                    },
                    time.hourOfDay, time.minuteOfHour, is24HourFormat(this)
            ).show()
        }

        viewModel.dateTimeObserver().observe(this, {
            btnDate.text = formatDate(this, it)
            btnTime.text = formatTime(this, it)
        })

        val editA1c = findViewById<TextInputEditText>(R.id.edit_a1c)
        editA1c.doAfterTextChanged {
            viewModel.setValue(it.toString())
        }

        var focusEdit = true
        viewModel.valueObserver().observe(this, { value ->
            if (value != editA1c.text.toString()) {
                editA1c.setText(value.toString())
                editA1c.setSelection(editA1c.length())
            }
            if (focusEdit) {
                focusEdit = false
                if (value.isNullOrEmpty()) {
                    editA1c.showKeyboard()
                } else {
                    editA1c.hideKeyboard()
                }
            }
        })

        val inputLayout = findViewById<TextInputLayout>(R.id.input_layout)
        viewModel.errorValueEmptyObserver().observe(this, {
            if (it) {
                inputLayout.error = getString(R.string.no_value)
                editA1c.showKeyboard()
            }
            inputLayout.isErrorEnabled = it
        })
        viewModel.actionFinishObserver().observe(this, { if (it) finish() })
        viewModel.errorSaveObserver().observe(this, {
            if (it) {
                Snackbar.make(toolbar, R.string.error_saving_log, LENGTH_INDEFINITE).show()
            }
        })
        viewModel.errorDeleteObserver().observe(this, {
            if (it) {
                Snackbar.make(toolbar, R.string.error_deleting_log, LENGTH_INDEFINITE).show()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(EXTRA_ID, viewModel.getId())
        outState.putString(EXTRA_A1C, viewModel.getValue())
        outState.putString(EXTRA_DATE_TIME, viewModel.getCurrentDateTime().toString())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_a1c, menu)

        val id = intent.getLongExtra(EXTRA_ID, 0L)
        menu?.findItem(R.id.menu_delete)?.isVisible = id != 0L
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_save -> viewModel.save()
            R.id.menu_delete -> {
                ConfirmDialog.Builder().from(this)
                        .title(R.string.title_confirm)
                        .message(R.string.msg_delete_a1c)
                        .positiveBtnText(R.string.action_delete)
                        .negativeBtnText(R.string.action_cancel)
                        .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onConfirm(tag: String?) {
        viewModel.delete()
    }
}