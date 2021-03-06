package com.axel_stein.glucose_tracker.ui.edit.edit_medication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.databinding.ActivityEditMedicationBinding
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog.OnConfirmListener
import com.axel_stein.glucose_tracker.utils.ui.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT

class EditMedicationActivity : AppCompatActivity(), OnConfirmListener {
    private val args: EditMedicationActivityArgs by navArgs()
    private val viewModel: EditMedicationViewModel by viewModels { EditMedicationFactory(this, args.id) }
    private lateinit var binding: ActivityEditMedicationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMedicationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupTitleEditor()
        setupDosageFormSpinner()
        setupDosageEditor()
        setupUnitsSpinner()

        setViewVisible(args.id != 0L, binding.btnToggleActive)
        binding.btnToggleActive.setOnClickListener { viewModel.toggleActive() }
        viewModel.activeLiveData.observe(this, {
            binding.btnToggleActive.setText(if (it) R.string.action_suspend_medication_taking else R.string.action_resume_medication_taking)
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

    private fun setupTitleEditor() {
        binding.editTitle.setupEditor {
            viewModel.setTitle(it)
        }

        viewModel.titleLiveData.observe(this, {
            binding.editTitle.setEditorText(it)
        })

        viewModel.errorEmptyTitleLiveData.observe(this, {
            binding.inputLayoutTitle.showEmptyFieldError(it)
        })
    }

    private fun setupDosageFormSpinner() {
        binding.formSpinner.setupSpinner(binding.inputLayoutForm) {
            viewModel.setDosageForm(it)
        }

        binding.formSpinner.setSpinnerItems(resources.getStringArray(R.array.dosage_forms))

        viewModel.dosageFormLiveData.observe(this, {
            binding.formSpinner.setSpinnerSelection(it)
        })
    }

    private fun setupDosageEditor() {
        binding.editDosage.setupEditor {
            viewModel.setDosage(it)
        }

        viewModel.dosageLiveData.observe(this, {
            binding.editDosage.setEditorText(it, false)
        })
    }

    private fun setupUnitsSpinner() {
        binding.unitsSpinner.setupSpinner(binding.inputLayoutUnits) {
            viewModel.setDosageUnit(it)
        }

        binding.unitsSpinner.setSpinnerItems(resources.getStringArray(R.array.dosage_units))

        viewModel.dosageUnitLiveData.observe(this, {
            binding.unitsSpinner.setSpinnerSelection(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_editor, menu)
        menu?.findItem(R.id.menu_delete)?.isVisible = args.id != 0L
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> viewModel.save()
            R.id.menu_delete -> {
                ConfirmDialog.Builder()
                    .from(this)
                    .title(R.string.dialog_title_confirm)
                    .message(R.string.dialog_msg_delete_medication)
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