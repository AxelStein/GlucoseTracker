package com.axel_stein.glucose_tracker.ui.edit_medication

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
import com.axel_stein.glucose_tracker.utils.*

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

        binding.btnToggleActive.setOnClickListener { viewModel.toggleActive() }
        viewModel.activeLiveData().observe(this, {
            binding.btnToggleActive.setText(if (it) R.string.suspend else R.string.resume)
        })

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

    private fun setupTitleEditor() {
        binding.editTitle.setupEditor {
            viewModel.setTitle(it)
        }

        viewModel.titleLiveData().observe(this, {
            binding.editTitle.setEditorText(it)
        })

        viewModel.errorEmptyTitleLiveData().observe(this, {
            binding.inputLayoutTitle.showEmptyFieldError(it)
        })
    }

    private fun setupDosageFormSpinner() {
        binding.formSpinner.setupSpinner(binding.inputLayoutForm) {
            viewModel.setDosageForm(it)
        }

        binding.formSpinner.setSpinnerItems(resources.getStringArray(R.array.dosage_forms))

        viewModel.dosageFormLiveData().observe(this, {
            binding.formSpinner.setSpinnerSelection(it)
        })
    }

    private fun setupDosageEditor() {
        binding.editDosage.setupEditor {
            viewModel.setDosage(it)
        }

        viewModel.dosageLiveData().observe(this, {
            binding.editDosage.setEditorText(it, false)
        })
    }

    private fun setupUnitsSpinner() {
        binding.unitsSpinner.setupSpinner(binding.inputLayoutUnits) {
            viewModel.setDosageUnit(it)
        }

        binding.unitsSpinner.setSpinnerItems(resources.getStringArray(R.array.dosage_units))

        viewModel.dosageUnitLiveData().observe(this, {
            binding.unitsSpinner.setSpinnerSelection(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_medication, menu)
        menu?.findItem(R.id.menu_delete)?.isVisible = args.id != 0L
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> viewModel.save()
            R.id.menu_delete -> {
                ConfirmDialog.Builder()
                    .from(this)
                    .title(R.string.title_confirm)
                    .message(R.string.msg_delete_medication)
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