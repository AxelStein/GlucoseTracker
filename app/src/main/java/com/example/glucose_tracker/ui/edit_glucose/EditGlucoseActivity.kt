package com.example.glucose_tracker.ui.edit_glucose

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.example.glucose_tracker.utils.formatDate
import com.example.glucose_tracker.utils.formatTime
import com.example.glucose_tracker.utils.is24HourFormat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.joda.time.DateTime

class EditGlucoseActivity: AppCompatActivity() {
    private val viewModel: EditGlucoseViewModel by viewModels()
    private var initInputGlucose = true
    private var initSpinner = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_glucose)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val btnDate = findViewById<TextView>(R.id.btn_date)
        btnDate.setOnClickListener {
            val date = viewModel.dateTime.value ?: DateTime()
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
            val time = viewModel.dateTime.value ?: DateTime()
            TimePickerDialog(this,
                    { _, hourOfDay, minuteOfHour ->
                        viewModel.setTime(hourOfDay, minuteOfHour)
                    },
                    time.hourOfDay, time.minuteOfHour, is24HourFormat(this)
            ).show()
        }

        viewModel.dateTime.observe(this, {
            btnDate.text = formatDate(this, it)
            btnTime.text = formatTime(this, it)
        })

        val inputLayoutGlucose = findViewById<TextInputLayout>(R.id.input_layout_glucose)
        viewModel.errorGlucoseEmpty.observe(this, {
            if (it) {
                inputLayoutGlucose.error = "There is no value"
            }
            inputLayoutGlucose.isErrorEnabled = it
        })
        viewModel.actionFinish.observe(this, { if (it) finish() })

        val editGlucose = findViewById<TextInputEditText>(R.id.edit_glucose)
        viewModel.glucose.observe(this, { value ->
            if (initInputGlucose) {
                initInputGlucose = false
                if (value > 0f) {
                    editGlucose.setText(value.toString())
                    editGlucose.setSelection(editGlucose.length())
                }
                editGlucose.doAfterTextChanged {
                    viewModel.setGlucose(it.toString())
                }
            }
        })

        val spinnerMeasured = findViewById<Spinner>(R.id.spinner_measured)
        viewModel.measured.observe(this, {
            if (initSpinner) {
                initSpinner = false
                spinnerMeasured.setSelection(it)

                spinnerMeasured.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        viewModel.setMeasured(position)
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_glucose, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_save -> {
                viewModel.save()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}