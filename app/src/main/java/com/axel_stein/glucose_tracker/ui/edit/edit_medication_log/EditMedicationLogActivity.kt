package com.axel_stein.glucose_tracker.ui.edit.edit_medication_log

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.databinding.ActivityEditMedicationLogBinding
import com.axel_stein.glucose_tracker.databinding.ActivityEditMedicationLogBinding.inflate
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog.OnConfirmListener
import com.axel_stein.glucose_tracker.utils.formatDate
import com.axel_stein.glucose_tracker.utils.formatRoundIfInt
import com.axel_stein.glucose_tracker.utils.formatTime
import com.axel_stein.glucose_tracker.utils.ui.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT

class EditMedicationLogActivity : AppCompatActivity(), OnConfirmListener {
    private val args: EditMedicationLogActivityArgs by navArgs()
    private val viewModel: EditMedicationLogViewModel by viewModels { EditMedicationLogFactory(this, args.id) }
    private lateinit var binding: ActivityEditMedicationLogBinding
    private var showSaveMenuItem = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDateTime()
        setupMedicationSpinner()
        setupAmountEditor()
        setupMeasuredSpinner()

        viewModel.editorActiveLiveData.observe(this, {
            showSaveMenuItem = it
            invalidateOptionsMenu()

            binding.btnDate.isEnabled = it
            binding.btnTime.isEnabled = it
            binding.inputLayoutMedication.isEnabled = it
            binding.inputLayoutAmount.isEnabled = it
            binding.inputLayoutMeasured.isEnabled = it
        })

        viewModel.errorLoadingLiveData.observe(this, {
            if (it) binding.errorLoading.visibility = View.VISIBLE
        })

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

    private fun setupMedicationSpinner() {
        binding.medicationSpinner.setupSpinner(binding.inputLayoutMedication) { position ->
            viewModel.selectMedication(position)
        }

        val dosageUnits = resources.getStringArray(R.array.dosage_units)
        viewModel.medicationListLiveData.observe(this, {
            binding.inputLayoutMedication.isEnabled = it.isNotEmpty()
            binding.medicationSpinner.setSpinnerItems(it.map { item ->
                if (item.dosageUnit >= 0) {
                    "${item.title} (${item.dosage.formatRoundIfInt()} ${dosageUnits[item.dosageUnit]})"
                } else {
                    item.title
                }
            })
        })
        viewModel.medicationSelectedLiveData.observe(this, {
            binding.medicationSpinner.setSpinnerSelection(it)
        })
        /*viewModel.errorMedicationListEmptyLiveData.observe(this, {
            binding.inputLayoutMedication.showError(it, R.string.error_medication_list_empty)
        })*/
    }

    private fun setupAmountEditor() {
        binding.editAmount.setupEditor {
            viewModel.setAmount(it)
        }
        viewModel.amountLiveData.observe(this, {
            binding.editAmount.setEditorText(it)
        })

        val dosageForms = resources.getStringArray(R.array.dosage_forms)
        viewModel.dosageFormLiveData.observe(this, {
            if (it >= 0) binding.inputLayoutAmount.suffixText = dosageForms[it]
        })
        viewModel.errorAmountEmptyLiveData.observe(this, {
            binding.inputLayoutAmount.showEmptyFieldError(it)
        })
    }

    private fun setupMeasuredSpinner() {
        binding.measuredSpinner.setupSpinner(binding.inputLayoutMeasured) { position ->
            viewModel.selectMeasured(position)
        }
        binding.measuredSpinner.setSpinnerItems(resources.getStringArray(R.array.measured))

        viewModel.measuredLiveData.observe(this, { position ->
            binding.measuredSpinner.setSpinnerSelection(position)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_editor, menu)
        menu?.findItem(R.id.menu_delete)?.isVisible = args.id != 0L
        menu?.findItem(R.id.menu_save)?.isVisible = showSaveMenuItem
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> viewModel.save()
            R.id.menu_delete -> {
                ConfirmDialog.Builder().from(this)
                    .title(R.string.dialog_title_confirm)
                    .message(R.string.dialog_msg_delete_insulin_log)
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