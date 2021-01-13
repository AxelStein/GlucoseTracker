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
        setupAmountEditor()
        setupDosageSpinner()
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

    private fun setupAmountEditor() {
        binding.editAmount.setupEditor {
            viewModel.setAmount(it)
        }

        viewModel.amountLiveData().observe(this, {
            binding.editTitle.setEditorText(it)
        })

        viewModel.errorEmptyAmountLiveData().observe(this, {
            binding.inputLayoutAmount.showEmptyFieldError(it)
        })
    }

    private fun setupDosageSpinner() {
        binding.dosageSpinner.setupSpinner(binding.inputLayoutDosage) {
            viewModel.setDosageUnits(it)
        }

        binding.dosageSpinner.setSpinnerItems(resources.getStringArray(R.array.dosage_units))

        viewModel.dosageLiveData().observe(this, {
            binding.dosageSpinner.setSpinnerSelection(it)
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