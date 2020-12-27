package com.axel_stein.glucose_tracker.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.utils.hideView
import com.axel_stein.glucose_tracker.utils.setItemSelectedListener
import com.axel_stein.glucose_tracker.utils.show

class StatisticsFragment: Fragment() {
    private val model: StatisticsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_statistics, container, false)
        val spinnerPeriod = root.findViewById<Spinner>(R.id.spinner_period)
        val textMin = root.findViewById<TextView>(R.id.text_min)
        val textMax = root.findViewById<TextView>(R.id.text_max)
        val textAvg = root.findViewById<TextView>(R.id.text_avg)
        val textA1C = root.findViewById<TextView>(R.id.text_a1c)
        val textControlGood = root.findViewById<TextView>(R.id.text_a1c_control_good)
        val textControlAvg = root.findViewById<TextView>(R.id.text_a1c_control_avg)
        val textControlBad = root.findViewById<TextView>(R.id.text_a1c_control_bad)

        model.getStatsObserver().observe(viewLifecycleOwner, { stats ->
            textMin.text = stats.minFormatted
            textMax.text = stats.maxFormatted
            textAvg.text = stats.avgFormatted
            textA1C.text = stats.a1cFormatted
        })
        model.getDiabetesControlObserver().observe(viewLifecycleOwner, {
            hideView(textControlGood, textControlAvg, textControlBad)
            when(it) {
                0 -> textControlGood.show()
                1 -> textControlAvg.show()
                2 -> textControlBad.show()
            }
        })

        spinnerPeriod.onItemSelectedListener = setItemSelectedListener {
            model.setPeriod(it)
        }
        return root
    }
}