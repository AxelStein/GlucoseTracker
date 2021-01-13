package com.axel_stein.glucose_tracker.ui.edit_note

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.databinding.ActivityEditNoteBinding
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog.OnConfirmListener
import com.axel_stein.glucose_tracker.utils.*
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar

class EditNoteActivity : AppCompatActivity(), OnConfirmListener {
    private val args: EditNoteActivityArgs by navArgs()
    private val viewModel: EditNoteViewModel by viewModels { EditNoteFactory(this, args.id) }
    private lateinit var binding: ActivityEditNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDateTime()
        setupEditNote()

        viewModel.errorSaveLiveData().observe(this, { error ->
            if (error) {
                Snackbar.make(binding.toolbar, R.string.error_saving_note, LENGTH_INDEFINITE).show()
            }
        })
        viewModel.errorDeleteLiveData().observe(this, { error ->
            if (error) {
                Snackbar.make(binding.toolbar, R.string.error_deleting_note, LENGTH_INDEFINITE).show()
            }
        })
        viewModel.actionFinishLiveData().observe(this, { if (it) finish() })
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupDateTime() {
        binding.btnDate.setOnClickListener {
            showDatePicker(this, viewModel.getCurrentDateTime()) { year, month, dayOfMonth ->
                viewModel.setDate(year, month, dayOfMonth)
            }
        }

        binding.btnTime.setOnClickListener {
            showTimePicker(this, viewModel.getCurrentDateTime()) { hourOfDay, minuteOfHour ->
                viewModel.setTime(hourOfDay, minuteOfHour)
            }
        }

        viewModel.dateTimeLiveData().observe(this, {
            binding.btnDate.text = formatDate(this, it)
            binding.btnTime.text = formatTime(this, it)
        })
    }

    private fun setupEditNote() {
        binding.editNote.setupEditor { text ->
            viewModel.setNote(text)
        }

        viewModel.noteLiveData().observe(this, { value ->
            binding.editNote.setEditorText(value)
        })

        viewModel.errorNoteEmptyLiveData().observe(this, { error ->
            binding.inputLayoutNote.showEmptyFieldError(error)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_note, menu)
        menu?.findItem(R.id.menu_delete)?.isVisible = args.id != 0L
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