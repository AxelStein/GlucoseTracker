package com.axel_stein.glucose_tracker.ui.edit_glucose

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.databinding.ActivityEditGlucoseBinding
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog.OnConfirmListener
import com.axel_stein.glucose_tracker.utils.*
import com.axel_stein.glucose_tracker.utils.ui.*
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import javax.inject.Inject


class EditGlucoseActivity: AppCompatActivity(), OnConfirmListener {
    private val args: EditGlucoseActivityArgs by navArgs()
    private val viewModel: EditGlucoseViewModel by viewModels { EditGlucoseFactory(this, args.id) }
    private lateinit var binding: ActivityEditGlucoseBinding

    @Inject
    lateinit var appSettings: AppSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)

        binding = ActivityEditGlucoseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDateTime()
        setupGlucoseEditor()
        setupMeasuredSpinner()

        viewModel.errorLoadingLiveData().observe(this, { error ->
            if (error) binding.errorLoading.visibility = View.VISIBLE
        })
        viewModel.errorSaveLiveData().observe(this, { error ->
            if (error) {
                Snackbar.make(binding.toolbar, R.string.error_saving_log, LENGTH_INDEFINITE).show()
            }
        })
        viewModel.errorDeleteLiveData().observe(this, { error ->
            if (error) {
                Snackbar.make(binding.toolbar, R.string.error_deleting_log, LENGTH_INDEFINITE).show()
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

    private fun getGlucoseEditor(): TextInputEditText {
        val useMmol = appSettings.useMmolAsGlucoseUnits()
        return if (useMmol) binding.editGlucoseMmol else binding.editGlucoseMg
    }

    private fun getInputLayout(): TextInputLayout {
        val useMmol = appSettings.useMmolAsGlucoseUnits()
        return if (useMmol) binding.inputLayoutMmol else binding.inputLayoutMg
    }

    private fun setupGlucoseEditor() {
        val editor = getGlucoseEditor()
        editor.setupEditor { text ->
            viewModel.setGlucose(text)
        }
        viewModel.glucoseLiveData().observe(this, { value ->
            editor.setEditorText(value)
        })

        val inputLayout = getInputLayout()
        inputLayout.show()

        viewModel.errorGlucoseEmptyLiveData().observe(this, { error ->
            inputLayout.showEmptyFieldError(error)
        })
    }

    private fun setupMeasuredSpinner() {
        binding.measuredSpinner.setupSpinner(binding.inputLayoutMeasured) { position ->
            viewModel.setMeasured(position)
        }
        binding.measuredSpinner.setSpinnerItems(resources.getStringArray(R.array.measured))

        viewModel.measuredLiveData().observe(this, { position ->
            binding.measuredSpinner.setSpinnerSelection(position)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_edit_glucose, menu)
        menu?.findItem(R.id.menu_delete)?.isVisible = args.id != 0L
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_save -> viewModel.save()
            R.id.menu_delete -> {
                ConfirmDialog.Builder().from(this)
                    .title(R.string.dialog_title_confirm)
                    .message(R.string.dialog_msg_delete_glucose)
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