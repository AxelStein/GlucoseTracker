package com.axel_stein.glucose_tracker.ui.statistics

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.utils.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.LineData


class StatisticsFragment: Fragment() {
    private val viewModel: StatisticsViewModel by viewModels()

    @SuppressLint("CheckResult")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_statistics, container, false)
        val content = root.findViewById<View>(R.id.content)
        val spinnerPeriod = root.findViewById<Spinner>(R.id.spinner_period)
        val textMin = root.findViewById<TextView>(R.id.text_min)
        val textMax = root.findViewById<TextView>(R.id.text_max)
        val textAvg = root.findViewById<TextView>(R.id.text_avg)
        val textA1C = root.findViewById<TextView>(R.id.text_a1c)
        val textControlGood = root.findViewById<TextView>(R.id.text_a1c_control_good)
        val textControlAvg = root.findViewById<TextView>(R.id.text_a1c_control_avg)
        val textControlBad = root.findViewById<TextView>(R.id.text_a1c_control_bad)
        val textNoData = root.findViewById<TextView>(R.id.text_no_data)
        val textError = root.findViewById<TextView>(R.id.text_error)

        val beforeMealChart = root.findViewById<LineChart>(R.id.glucose_chart_before_meal)
        setupChartView(beforeMealChart)
        addChartLimitLines(beforeMealChart, 5.5f, 7f, 3f)

        viewModel.beforeMealChartLiveData().observe(viewLifecycleOwner, {
            setChartLineData(beforeMealChart, it)
        })
        viewModel.beforeMealMaxLiveData().observe(viewLifecycleOwner, {
            beforeMealChart.axisLeft.axisMaximum = if (it < 10f) 10f else it + 2f
        })

        val afterMealChart = root.findViewById<LineChart>(R.id.glucose_chart_after_meal)
        setupChartView(afterMealChart)
        addChartLimitLines(afterMealChart, 7.8f, 11f, 3.5f)

        viewModel.afterMealChartLiveData().observe(viewLifecycleOwner, {
            setChartLineData(afterMealChart, it)
        })
        viewModel.afterMealMaxLiveData().observe(viewLifecycleOwner, {
            afterMealChart.axisLeft.axisMaximum = if (it < 10f) 10f else it + 2f
        })

        val a1cChart = root.findViewById<LineChart>(R.id.a1c_chart)
        setupChartView(a1cChart)
        addChartLimitLines(a1cChart, 6f, 7f, 8f)

        viewModel.a1cChartLiveData().observe(viewLifecycleOwner, {
            setChartLineData(a1cChart, it)
        })
        viewModel.a1cMaxLiveData().observe(viewLifecycleOwner, {
            a1cChart.axisLeft.axisMaximum = if (it < 10f) 10f else it + 2f
        })

        viewModel.statsLiveData().observe(viewLifecycleOwner, { stats ->
            if (stats != null) {
                textMin.text = stats.minFormatted
                textMax.text = stats.maxFormatted
                textAvg.text = stats.avgFormatted
                textA1C.text = stats.a1cFormatted
                content.show()
                textNoData.hide()
            } else {
                content.hide()
                textNoData.show()
            }
        })

        viewModel.showErrorLiveData().observe(viewLifecycleOwner, {
            setViewVisible(it, textError)
        })

        viewModel.diabetesControlLiveData().observe(viewLifecycleOwner, {
            hideView(textControlGood, textControlAvg, textControlBad)
            when (it) {
                0 -> textControlGood.show()
                1 -> textControlAvg.show()
                2 -> textControlBad.show()
            }
        })

        spinnerPeriod.onItemSelectedListener = setItemSelectedListener {
            viewModel.setPeriod(it)
        }
        return root
    }

    private fun setupChartView(chart: LineChart) {
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.xAxis.setDrawLabels(false)
        chart.xAxis.setDrawGridLines(false)
        chart.axisLeft.axisMinimum = 0f
        chart.axisRight.setDrawLabels(false)
        chart.isDoubleTapToZoomEnabled = false
        chart.setPinchZoom(false)
    }

    private fun setChartLineData(chart: LineChart, data: LineData) {
        chart.data = data
        chart.notifyDataSetChanged()
        chart.setVisibleXRangeMaximum(20f)
        chart.invalidate()
    }

    private fun addChartLimitLines(chart: LineChart, vararg limits: Float) {
        for (limit in limits) {
            chart.axisLeft.addLimitLine(LimitLine(limit))
        }
    }
}