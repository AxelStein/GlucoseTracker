package com.example.glucose_tracker.ui.edit_note

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import com.example.glucose_tracker.R
import com.example.glucose_tracker.data.model.LogItem
import com.example.glucose_tracker.ui.dialogs.ConfirmDialog
import com.example.glucose_tracker.ui.dialogs.ConfirmDialog.OnConfirmListener
import com.example.glucose_tracker.utils.formatDate
import com.example.glucose_tracker.utils.formatTime
import com.example.glucose_tracker.utils.is24HourFormat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EdiNoteActivity : AppCompatActivity(), OnConfirmListener {
    companion object {
        private const val EXTRA_ID = "com.example.glucose_tracker.ui.edit_note.EXTRA_ID"
        private const val EXTRA_DATE_TIME = "com.example.glucose_tracker.ui.edit_note.EXTRA_DATE_TIME"
        private const val EXTRA_NOTE = "com.example.glucose_tracker.ui.edit_note.EXTRA_NOTE"

        fun launch(context: Context) {
            context.startActivity(Intent(context, EdiNoteActivity::class.java))
        }

        fun launch(context: Context, item: LogItem) {
            val intent = Intent(context, EdiNoteActivity::class.java)
            intent.putExtra(EXTRA_ID, item.id)
            context.startActivity(intent)
        }
    }

    private val viewModel: EditNoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        if (savedInstanceState != null && viewModel.shouldRestore()) {
            val id = savedInstanceState.getLong(EXTRA_ID)
            val dateTime = savedInstanceState.getString(EXTRA_DATE_TIME)
            val note = savedInstanceState.getString(EXTRA_NOTE)
            viewModel.restore(id, dateTime, note)
        } else {
            viewModel.loadData(intent.getLongExtra(EXTRA_ID, 0L))
        }

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

        val inputLayoutNote = findViewById<TextInputLayout>(R.id.input_layout_note)
        viewModel.errorNoteEmptyObserver().observe(this, {
            if (it) {
                inputLayoutNote.error = getString(R.string.no_value)
            }
            inputLayoutNote.isErrorEnabled = it
        })
        viewModel.actionFinishObserver().observe(this, { if (it) finish() })
        viewModel.errorSaveObserver().observe(this, {
            if (it) {
                Snackbar.make(toolbar, R.string.error_saving_note, BaseTransientBottomBar.LENGTH_INDEFINITE).show()
            }
        })
        viewModel.errorDeleteObserver().observe(this, {
            if (it) {
                Snackbar.make(toolbar, R.string.error_deleting_note, BaseTransientBottomBar.LENGTH_INDEFINITE).show()
            }
        })

        val editNote = findViewById<TextInputEditText>(R.id.edit_note)
        editNote.doAfterTextChanged {
            viewModel.setNote(it.toString())
        }
        viewModel.noteObserver().observe(this, { value ->
            if (value != editNote.text.toString()) {
                editNote.setText(value.toString())
                editNote.setSelection(editNote.length())
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(EXTRA_ID, viewModel.getId())
        outState.putString(EXTRA_DATE_TIME, viewModel.getCurrentDateTime().toString())
        outState.putString(EXTRA_NOTE, viewModel.getNote())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_note, menu)

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
                        .message(R.string.msg_delete_note)
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