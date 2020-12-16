package com.example.glucose_tracker.ui.edit_glucose

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import com.example.glucose_tracker.R
import com.example.glucose_tracker.data.model.LogItem
import com.example.glucose_tracker.ui.dialogs.ConfirmDialog
import com.example.glucose_tracker.ui.dialogs.ConfirmDialog.OnConfirmListener
import com.example.glucose_tracker.utils.formatDate
import com.example.glucose_tracker.utils.formatTime
import com.example.glucose_tracker.utils.is24HourFormat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EditGlucoseActivity: AppCompatActivity(), OnConfirmListener {
    companion object {
        private const val EXTRA_ID = "com.example.glucose_tracker.ui.edit_glucose.EXTRA_ID"

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
        toolbar.setNavigationOnClickListener { finish() }

        viewModel.loadData(intent.getLongExtra(EXTRA_ID, 0L))

        val btnDate = findViewById<TextView>(R.id.btn_date)
        btnDate.setOnClickListener {
            val date = viewModel.getCurrentDateTime()
            DatePickerDialog(
                    this,
                    { _, year, month, dayOfMonth ->
                        viewModel.setDate(year, month+1, dayOfMonth)
                    },
                    date.year, date.monthOfYear-1, date.dayOfMonth
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

        val inputLayoutGlucose = findViewById<TextInputLayout>(R.id.input_layout_glucose)
        viewModel.errorGlucoseEmptyObserver().observe(this, {
            if (it) {
                inputLayoutGlucose.error = "There is no value"
            }
            inputLayoutGlucose.isErrorEnabled = it
        })
        viewModel.actionFinishObserver().observe(this, { if (it) finish() })

        val editGlucose = findViewById<TextInputEditText>(R.id.edit_glucose)
        editGlucose.doAfterTextChanged {
            viewModel.setGlucose(it.toString())
        }
        viewModel.glucoseObserver().observe(this, { value ->
            if (value != editGlucose.text.toString()) {
                editGlucose.setText(value.toString())
                editGlucose.setSelection(editGlucose.length())
            }
        })

        val spinnerMeasured = findViewById<Spinner>(R.id.spinner_measured)
        spinnerMeasured.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setMeasured(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        viewModel.measuredObserver().observe(this, { value ->
            if (value != spinnerMeasured.selectedItemPosition) {
                spinnerMeasured.setSelection(value)
            }
        })
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
                        .title("Are you sure?")
                        .message("Log will be permanently deleted.")
                        .positiveBtnText("delete")
                        .negativeBtnText("cancel")
                        .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onConfirm(tag: String?) {
        viewModel.delete()
    }
}