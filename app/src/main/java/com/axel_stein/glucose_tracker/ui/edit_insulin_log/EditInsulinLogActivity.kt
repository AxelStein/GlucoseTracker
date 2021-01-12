package com.axel_stein.glucose_tracker.ui.edit_insulin_log

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.databinding.ActivityEditInsulinLogBinding
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog.OnConfirmListener
import com.axel_stein.glucose_tracker.utils.*
import org.joda.time.MutableDateTime

class EditInsulinLogActivity : AppCompatActivity(), OnConfirmListener {
    private val args: EditInsulinLogActivityArgs by navArgs()
    private val viewModel: EditInsulinLogViewModel by viewModels { EditInsulinLogFactory(this, args.id) }
    private lateinit var binding: ActivityEditInsulinLogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditInsulinLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupDateTime()
        setupInsulinSpinner()
        setupUnits()
        setupMeasuredDropDown()

        viewModel.actionFinishLiveData().observe(this, {
            if (it) finish()
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

        viewModel.dateTimeLiveData().observe(this, {
            binding.btnDate.text = formatDate(this, it)
            binding.btnTime.text = formatTime(this, it)
        })
    }

    private fun setupInsulinSpinner() {
        binding.insulinSpinner.setupSpinner(binding.inputLayoutInsulin) { position ->
            viewModel.selectInsulin(position)
        }
        viewModel.insulinLiveData().observe(this, {
            binding.insulinSpinner.setSpinnerItems(it)
        })
        viewModel.insulinSelectedLiveData().observe(this, { position ->
            binding.insulinSpinner.setSpinnerSelection(position)
        })
    }

    private fun setupUnits() {
        binding.editUnits.setupEditor { text ->
            viewModel.setUnits(text)
        }
        viewModel.unitsLiveData().observe(this, { units ->
            binding.editUnits.setEditorText(units)
        })
        viewModel.errorUnitsEmptyLiveData().observe(this, { error ->
            binding.inputLayoutUnits.showEmptyFieldError(binding.editUnits, error)
        })
    }

    private fun setupMeasuredDropDown() {
        binding.measuredSpinner.setupSpinner(binding.inputLayoutMeasured) { position ->
            viewModel.selectMeasured(position)
        }
        binding.measuredSpinner.setSpinnerItems(resources.getStringArray(R.array.measured))

        viewModel.measuredLiveData().observe(this, { position ->
            binding.measuredSpinner.setSpinnerSelection(position)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_insulin_log, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> viewModel.save()
            R.id.menu_delete -> {
                ConfirmDialog.Builder().from(this)
                    .title(R.string.title_confirm)
                    .message(R.string.msg_delete_insulin_log)
                    .positiveBtnText(R.string.action_delete)
                    .negativeBtnText(R.string.action_cancel)
                    .show()
            }
        }
        return true
    }

    override fun onConfirm(tag: String?) {
        viewModel.delete()
    }
}