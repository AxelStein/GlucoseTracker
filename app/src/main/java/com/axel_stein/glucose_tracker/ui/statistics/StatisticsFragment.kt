package com.axel_stein.glucose_tracker.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.axel_stein.glucose_tracker.R

class StatisticsFragment: Fragment() {
    private val model: StatisticsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_statistics, container, false)
        val spinnerPeriod = root.findViewById<Spinner>(R.id.spinner_period)
        val textMin = root.findViewById<TextView>(R.id.text_min)
        val textMax = root.findViewById<TextView>(R.id.text_max)
        val textAvg = root.findViewById<TextView>(R.id.text_avg)
        val textA1C = root.findViewById<TextView>(R.id.text_a1c)
        val textControl = root.findViewById<TextView>(R.id.text_a1c_control)

        model.getStatsObserver().observe(viewLifecycleOwner, { stats ->
            textMin.text = stats.minFormatted
            textMax.text = stats.maxFormatted
            textAvg.text = stats.avgFormatted
            textA1C.text = stats.a1cFormatted
        })
        model.getDiabetesControlObserver().observe(viewLifecycleOwner, {
            textControl.text = getString(when(it) {
                0 -> R.string.diabetes_control_good
                1 -> R.string.diabetes_control_average
                else -> R.string.diabetes_control_bad
            })

            val color = ContextCompat.getColor(requireContext(), when(it) {
                0 -> R.color.color_good_diabetes_control
                1 -> R.color.color_avg_diabetes_control
                else -> R.color.color_bad_diabetes_control
            })
            textControl.setTextColor(color)
        })

        spinnerPeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                model.setPeriod(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        return root
    }
}