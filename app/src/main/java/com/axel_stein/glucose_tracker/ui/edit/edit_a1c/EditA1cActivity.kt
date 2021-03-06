package com.axel_stein.glucose_tracker.ui.edit.edit_a1c

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.databinding.ActivityEditA1cBinding
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog.OnConfirmListener
import com.axel_stein.glucose_tracker.utils.formatDate
import com.axel_stein.glucose_tracker.utils.formatTime
import com.axel_stein.glucose_tracker.utils.ui.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT

class EditA1cActivity: AppCompatActivity(), OnConfirmListener {
    private val args: EditA1cActivityArgs by navArgs()
    private val viewModel: EditA1cViewModel by viewModels { EditA1cFactory(this, args.id) }
    private lateinit var binding: ActivityEditA1cBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditA1cBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDateTime()
        setupEditor()

        viewModel.showMessageLiveData.observe(this, {
            val msg = it.getContent()
            if (msg != null) {
                Snackbar.make(binding.root, msg, LENGTH_SHORT).show()
            }
        })
        viewModel.actionFinishLiveData.observe(this, {
            it.handleEvent()
            finish()
        })
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

        viewModel.dateTimeLiveData.observe(this, {
            binding.btnDate.text = formatDate(this, it)
            binding.btnTime.text = formatTime(this, it)
        })
    }

    private fun setupEditor() {
        binding.editA1c.setupEditor { text ->
            viewModel.setValue(text)
        }

        viewModel.a1cLiveData.observe(this, { value ->
            binding.editA1c.setEditorText(value)
        })

        viewModel.errorValueEmptyLiveData.observe(this, { error ->
            binding.inputLayout.showEmptyFieldError(error)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_editor, menu)
        menu?.findItem(R.id.menu_delete)?.isVisible = args.id != 0L
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_save -> viewModel.save()
            R.id.menu_delete -> {
                ConfirmDialog.Builder().from(this)
                    .title(R.string.dialog_title_confirm)
                    .message(R.string.dialog_msg_delete_a1c)
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