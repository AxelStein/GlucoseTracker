package com.example.glucose_tracker.ui.edit_glucose

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import com.example.glucose_tracker.R
import com.example.glucose_tracker.data.model.LogItem
import com.example.glucose_tracker.ui.dialogs.ConfirmDialog
import com.example.glucose_tracker.ui.dialogs.ConfirmDialog.OnConfirmListener
import com.example.glucose_tracker.utils.*
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class EditGlucoseActivity: AppCompatActivity(), OnConfirmListener {
    companion object {
        private const val EXTRA_ID = "com.example.glucose_tracker.ui.edit_glucose.EXTRA_ID"
        private const val EXTRA_DATE_TIME = "com.example.glucose_tracker.ui.edit_glucose.EXTRA_DATE_TIME"
        private const val EXTRA_GLUCOSE = "com.example.glucose_tracker.ui.edit_glucose.EXTRA_GLUCOSE"
        private const val EXTRA_MEASURED = "com.example.glucose_tracker.ui.edit_glucose.EXTRA_MEASURED"

        fun launch(context: Context) {
            context.startActivity(Intent(context, EditGlucoseActivity::class.java))
        }

        fun launch(context: Context, item: LogItem) {
            val intent = Intent(context, EditGlucoseActivity::class.java)
            intent.putExtra(EXTRA_ID, item.id)
            context.startActivity(intent)
        }
    }

    private val viewModel: EditGlucoseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_glucose)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { finish() }

        if (savedInstanceState != null && viewModel.shouldRestore()) {
            val id = savedInstanceState.getLong(EXTRA_ID)
            val dateTime = savedInstanceState.getString(EXTRA_DATE_TIME)
            val glucose = savedInstanceState.getFloat(EXTRA_GLUCOSE)
            val measured = savedInstanceState.getInt(EXTRA_MEASURED)
            viewModel.restore(id, dateTime, glucose, measured)
        } else {
            viewModel.loadData(intent.getLongExtra(EXTRA_ID, 0L))
        }

        val btnDate = findViewById<TextView>(R.id.btn_date)
        btnDate.setOnClickListener {
            val date = viewModel.getCurrentDateTime()
            DatePickerDialog(
                    this,
                    { _, year, month, dayOfMonth ->
                        viewModel.setDate(year, month + 1, dayOfMonth)
                    },
                    date.year, date.monthOfYear - 1, date.dayOfMonth
            ).show()
        }

        val btnTime = findViewById<TextView>(R.id.btn_time)
        btnTime.setOnClickListener {
            val time = viewModel.getCurrentDateTime()
            TimePickerDialog(this,
                    { _, hourOfDay, minuteOfHour ->
                        viewModel.setTime(hourOfDay, minuteOfHour)
                    },
                    time.hourOfDay, time.minuteOfHour, is24HourFormat(this)
            ).show()
        }

        viewModel.dateTimeObserver().observe(this, {
            btnDate.text = formatDate(this, it)
            btnTime.text = formatTime(this, it)
        })

        val editGlucose = findViewById<TextInputEditText>(R.id.edit_glucose)
        editGlucose.doAfterTextChanged {
            viewModel.setGlucose(it.toString())
        }
        editGlucose.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == IME_ACTION_DONE) {
                (v as EditText).hideKeyboard()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        var focusEdit = true
        viewModel.glucoseObserver().observe(this, { value ->
            if (value != editGlucose.text.toString()) {
                editGlucose.setText(value.toString())
                editGlucose.setSelection(editGlucose.length())
            }
            if (focusEdit) {
                focusEdit = false
                if (value.isNullOrEmpty()) {
                    editGlucose.showKeyboard()
                } else {
                    editGlucose.hideKeyboard()
                }
            }
        })

        val inputLayoutGlucose = findViewById<TextInputLayout>(R.id.input_layout_glucose)
        viewModel.errorGlucoseEmptyObserver().observe(this, {
            if (it) {
                inputLayoutGlucose.error = getString(R.string.no_value)
                editGlucose.showKeyboard()
            }
            inputLayoutGlucose.isErrorEnabled = it
        })
        viewModel.actionFinishObserver().observe(this, { if (it) finish() })
        viewModel.errorSaveObserver().observe(this, {
            if (it) {
                Snackbar.make(toolbar, R.string.error_saving_log, BaseTransientBottomBar.LENGTH_INDEFINITE).show()
            }
        })
        viewModel.errorDeleteObserver().observe(this, {
            if (it) {
                Snackbar.make(toolbar, R.string.error_deleting_log, BaseTransientBottomBar.LENGTH_INDEFINITE).show()
            }
        })

        val adapter = CArrayAdapter(
                this,
                R.layout.dropdown_menu_popup_item,
                resources.getStringArray(R.array.measured)
        )

        val measuredDropdown = findViewById<AutoCompleteTextView>(R.id.measured_dropdown)
        measuredDropdown.inputType = 0  // disable ime input
        measuredDropdown.setOnKeyListener { _, _, _ -> true }  // disable hardware keyboard input
        measuredDropdown.setAdapter(adapter)
        measuredDropdown.setOnClickListener { editGlucose.hideKeyboard() }

        val inputLayoutMeasured = findViewById<TextInputLayout>(R.id.input_layout_measured)
        inputLayoutMeasured.setEndIconOnClickListener {
            // override default behavior in order to close ime
            measuredDropdown.performClick()
        }
        measuredDropdown.setOnItemClickListener { _, _, position, _ ->
            inputLayoutMeasured.clearFocus()
            viewModel.setMeasured(position)
        }

        viewModel.measuredObserver().observe(this, { value ->
            if (value != measuredDropdown.listSelection) {
                measuredDropdown.listSelection = value
                measuredDropdown.setText(adapter.getItem(value), false)
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(EXTRA_ID, viewModel.getId())
        outState.putString(EXTRA_DATE_TIME, viewModel.getCurrentDateTime().toString())
        outState.putFloat(EXTRA_GLUCOSE, viewModel.getGlucoseValue())
        outState.putInt(EXTRA_MEASURED, viewModel.getMeasured())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_glucose, menu)

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