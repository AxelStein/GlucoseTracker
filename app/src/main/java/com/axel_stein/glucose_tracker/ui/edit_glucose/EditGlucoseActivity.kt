package com.axel_stein.glucose_tracker.ui.edit_glucose

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.navArgs
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.databinding.ActivityEditGlucoseBinding
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog.OnConfirmListener
import com.axel_stein.glucose_tracker.utils.*
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.joda.time.MutableDateTime
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
        setupMeasured()

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
        editor.doAfterTextChanged {
            viewModel.setGlucose(it.toString())
        }
        editor.setOnEditorActionListener { v, actionId, _ ->
            var consumed = false
            if (actionId == IME_ACTION_DONE) {
                (v as EditText).hideKeyboard()
                consumed = true
            }
            consumed
        }

        var focusEdit = true
        viewModel.glucoseLiveData().observe(this, { value ->
            if (value != editor.text.toString()) {
                editor.setText(value.toString())
                editor.setSelection(editor.length())
            }
            if (focusEdit) {
                focusEdit = false
                if (value.isNullOrEmpty()) {
                    editor.showKeyboard()
                } else {
                    editor.hideKeyboard()
                }
            }
        })

        val inputLayout = getInputLayout()
        inputLayout.show()

        viewModel.errorGlucoseEmptyLiveData().observe(this, { error ->
            if (error) {
                inputLayout.error = getString(R.string.no_value)
                editor.showKeyboard()
            }
            inputLayout.isErrorEnabled = error
        })
    }

    private fun setupMeasured() {
        val adapter = CArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            resources.getStringArray(R.array.measured)
        )

        binding.measuredDropdown.inputType = 0  // disable ime input
        binding.measuredDropdown.setOnKeyListener { _, _, _ -> true }  // disable hardware keyboard input
        binding.measuredDropdown.setAdapter(adapter)
        binding.measuredDropdown.setOnClickListener { getGlucoseEditor().hideKeyboard() }

        binding.inputLayoutMeasured.setEndIconOnClickListener {
            // override default behavior in order to close ime
            binding.measuredDropdown.performClick()
        }
        binding.measuredDropdown.setOnItemClickListener { _, _, position, _ ->
            binding.inputLayoutMeasured.clearFocus()
            viewModel.setMeasured(position)
        }
        binding.measuredDropdown.setOnDismissListener { binding.inputLayoutMeasured.clearFocus() }

        viewModel.measuredLiveData().observe(this, { value ->
            if (value != binding.measuredDropdown.listSelection) {
                binding.measuredDropdown.listSelection = value
                binding.measuredDropdown.setText(adapter.getItem(value), false)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_glucose, menu)
        menu?.findItem(R.id.menu_delete)?.isVisible = args.id != 0L
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_save -> viewModel.save()
            R.id.menu_delete -> {
                ConfirmDialog.Builder().from(this)
                    .title(R.string.title_confirm)
                    .message(R.string.msg_delete_glucose)
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