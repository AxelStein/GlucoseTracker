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
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.MutableDateTime

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
            val date = viewModel.date.value ?: LocalDate()
            DatePickerDialog(
                    this,
                    { _, year, month, dayOfMonth ->
                        viewModel.setDate(LocalDate(year, month+1, dayOfMonth))
                    },
                    date.year, date.monthOfYear-1, date.dayOfMonth
            ).show()
        }
        viewModel.date.observe(this, {
            btnDate.text = formatDate(this, it)
        })

        val btnTime = findViewById<TextView>(R.id.btn_time)
        btnTime.setOnClickListener {
            val time = viewModel.time.value ?: LocalTime()
            TimePickerDialog(this,
                    { _, hourOfDay, minuteOfHour ->
                        viewModel.setTime(LocalTime(hourOfDay, minuteOfHour))
                    },
                    time.hourOfDay, time.minuteOfHour, is24HourFormat(this)
            ).show()
        }
        viewModel.time.observe(this, {
            val dateTime = MutableDateTime.now()
            dateTime.hourOfDay = it.hourOfDay
            dateTime.minuteOfHour = it.minuteOfHour
            btnTime.text = formatTime(this, dateTime.millis)
        })

        val inputGlucose = findViewById<TextInputEditText>(R.id.input_glucose)
        viewModel.glucose.observe(this, { value ->
            if (initInputGlucose) {
                initInputGlucose = false
                if (value > 0f) {
                    inputGlucose.setText(value.toString())
                    inputGlucose.setSelection(inputGlucose.length())
                }
                inputGlucose.doAfterTextChanged {
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
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}