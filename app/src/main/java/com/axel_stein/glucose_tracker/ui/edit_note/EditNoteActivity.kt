package com.axel_stein.glucose_tracker.ui.edit_note

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.model.LogItem
import com.axel_stein.glucose_tracker.databinding.ActivityEditNoteBinding
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog.OnConfirmListener
import com.axel_stein.glucose_tracker.utils.*
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import org.joda.time.MutableDateTime

class EditNoteActivity : AppCompatActivity(), OnConfirmListener {
    companion object {
        const val EXTRA_ID = "com.axel_stein.glucose_tracker.ui.edit_note.EXTRA_ID"
        const val EXTRA_DATE_TIME = "com.axel_stein.glucose_tracker.ui.edit_note.EXTRA_DATE_TIME"
        const val EXTRA_NOTE = "com.axel_stein.glucose_tracker.ui.edit_note.EXTRA_NOTE"

        fun launch(context: Context) {
            context.startActivity(Intent(context, EditNoteActivity::class.java))
        }

        fun launch(context: Context, item: LogItem) {
            val intent = Intent(context, EditNoteActivity::class.java)
            intent.putExtra(EXTRA_ID, item.id)
            context.startActivity(intent)
        }
    }

    private lateinit var viewModel: EditNoteViewModel
    private lateinit var binding: ActivityEditNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.getLongExtra(EXTRA_ID, 0L)
        viewModel = ViewModelProvider(this, EditNoteFactory(id, savedInstanceState))
            .get(EditNoteViewModel::class.java)

        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDateTime()
        setupEditNote()

        viewModel.errorSaveObserver().observe(this, { error ->
            if (error) {
                Snackbar.make(binding.toolbar, R.string.error_saving_note, LENGTH_INDEFINITE).show()
            }
        })
        viewModel.errorDeleteObserver().observe(this, { error ->
            if (error) {
                Snackbar.make(binding.toolbar, R.string.error_deleting_note, LENGTH_INDEFINITE).show()
            }
        })
        viewModel.actionFinishObserver().observe(this, { if (it) finish() })
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupDateTime() {
        binding.btnDate.setOnClickListener {
            val date = viewModel.getCurrentDateTime()
            val dialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    viewModel.setDate(year, month + 1, dayOfMonth)
                },
                date.year, date.monthOfYear - 1, date.dayOfMonth
            )
            dialog.datePicker.maxDate = MutableDateTime().millis  // today
            dialog.show()
        }

        binding.btnTime.setOnClickListener {
            val time = viewModel.getCurrentDateTime()
            TimePickerDialog(this,
                { _, hourOfDay, minuteOfHour ->
                    viewModel.setTime(hourOfDay, minuteOfHour)
                },
                time.hourOfDay, time.minuteOfHour, is24HourFormat(this)
            ).show()
        }

        viewModel.dateTimeObserver().observe(this, {
            binding.btnDate.text = formatDate(this, it)
            binding.btnTime.text = formatTime(this, it)
        })
    }

    private fun setupEditNote() {
        binding.editNote.doAfterTextChanged {
            viewModel.setNote(it.toString())
        }

        var focusEdit = true
        viewModel.noteObserver().observe(this, { value ->
            if (value != binding.editNote.text.toString()) {
                binding.editNote.setText(value.toString())
                binding.editNote.setSelection(binding.editNote.length())
            }
            if (focusEdit) {
                focusEdit = false
                if (value.isNullOrEmpty()) {
                    binding.editNote.showKeyboard()
                } else {
                    binding.editNote.hideKeyboard()
                }
            }
        })

        viewModel.errorNoteEmptyObserver().observe(this, { error ->
            if (error) {
                binding.inputLayoutNote.error = getString(R.string.no_value)
                binding.editNote.showKeyboard()
            }
            binding.inputLayoutNote.isErrorEnabled = error
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