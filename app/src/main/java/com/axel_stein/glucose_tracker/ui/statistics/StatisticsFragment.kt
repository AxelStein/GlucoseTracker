package com.axel_stein.glucose_tracker.ui.statistics

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.ui.statistics.helpers.ChartColors
import com.axel_stein.glucose_tracker.ui.statistics.helpers.LabelValueFormatter
import com.axel_stein.glucose_tracker.utils.hide
import com.axel_stein.glucose_tracker.utils.hideView
import com.axel_stein.glucose_tracker.utils.setItemSelectedListener
import com.axel_stein.glucose_tracker.utils.show
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.google.android.material.color.MaterialColors


class StatisticsFragment: Fragment() {
    private lateinit var viewModel: StatisticsViewModel

    @SuppressLint("CheckResult")
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
        val textNoData = root.findViewById<TextView>(R.id.text_no_data)  // todo
        val textError = root.findViewById<TextView>(R.id.text_error)

        val beforeMealChart = root.findViewById<LineChart>(R.id.glucose_chart_before_meal)
        setupChartView(beforeMealChart)

        viewModel = ViewModelProvider(this, StatisticsFactory(ChartColors(requireActivity())))
                .get(StatisticsViewModel::class.java)

        viewModel.beforeMealChartLiveData().observe(viewLifecycleOwner, {
            setChartLineData(beforeMealChart, it)

            root.findViewById<View>(R.id.card_view_before_meal).show()
            textNoData.hide()
        })
        viewModel.beforeMealMaxLiveData().observe(viewLifecycleOwner, {
            beforeMealChart.axisLeft.axisMaximum = viewModel.axisMaximum(it)
        })
        viewModel.beforeMealLimitsLiveData().observe(viewLifecycleOwner, {
            addChartLimitLines(beforeMealChart, it)
        })
        viewModel.beforeMealLabelsLiveData().observe(viewLifecycleOwner, {
            beforeMealChart.xAxis.valueFormatter = LabelValueFormatter(it)
        })

        val afterMealChart = root.findViewById<LineChart>(R.id.glucose_chart_after_meal)
        setupChartView(afterMealChart)

        viewModel.afterMealChartLiveData().observe(viewLifecycleOwner, {
            setChartLineData(afterMealChart, it)

            root.findViewById<View>(R.id.card_view_after_meal).show()
            textNoData.hide()
        })
        viewModel.afterMealMaxLiveData().observe(viewLifecycleOwner, {
            afterMealChart.axisLeft.axisMaximum = viewModel.axisMaximum(it)
        })
        viewModel.afterMealLimitsLiveData().observe(viewLifecycleOwner, {
            addChartLimitLines(afterMealChart, it)
        })
        viewModel.afterMealLabelsLiveData().observe(viewLifecycleOwner, {
            afterMealChart.xAxis.valueFormatter = LabelValueFormatter(it)
        })

        val a1cChart = root.findViewById<LineChart>(R.id.a1c_chart)
        setupChartView(a1cChart)
        addChartLimitLines(a1cChart, arrayListOf(6f, 7f, 8f))

        viewModel.a1cChartLiveData().observe(viewLifecycleOwner, {
            setChartLineData(a1cChart, it)

            root.findViewById<View>(R.id.card_view_a1c).show()
            textNoData.hide()
        })
        viewModel.a1cMaxLiveData().observe(viewLifecycleOwner, {
            a1cChart.axisLeft.axisMaximum = if (it < 10f) 10f else it + 2f
        })
        viewModel.a1cLabelsLiveData().observe(viewLifecycleOwner, {
            a1cChart.xAxis.valueFormatter = LabelValueFormatter(it)
        })

        viewModel.statsLiveData().observe(viewLifecycleOwner, { stats ->
            if (stats != null) {
                textMin.text = stats.minFormatted
                textMax.text = stats.maxFormatted
                textAvg.text = stats.avgFormatted
                textA1C.text = stats.a1cFormatted

                root.findViewById<View>(R.id.card_view_stats).show()
                textNoData.hide()
            } else {
                root.findViewById<View>(R.id.card_view_stats).hide()
                textNoData.show()
            }
        })

        viewModel.showErrorLiveData().observe(viewLifecycleOwner, {
            if (it) {
                textError.show()
                textNoData.hide()
            } else {
                textError.hide()
            }
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
        // chart.xAxis.setDrawLabels(false)
        // chart.xAxis.setDrawGridLines(false)
        chart.xAxis.textColor = MaterialColors.getColor(requireActivity(), R.attr.chartTextColor, Color.BLACK)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.axisLeft.axisMinimum = 0f
        chart.axisLeft.textColor = MaterialColors.getColor(requireActivity(), R.attr.chartTextColor, Color.BLACK)
        chart.axisRight.setDrawLabels(false)
        chart.isDoubleTapToZoomEnabled = false
    }

    private fun setChartLineData(chart: LineChart, data: LineData?) {
        chart.data = data
        chart.notifyDataSetChanged()
        chart.setVisibleXRangeMaximum(10f)
        if (data != null) {
            chart.xAxis.granularity = 1f
            chart.xAxis.labelCount = data.entryCount
        }
        chart.invalidate()
    }

    private fun addChartLimitLines(chart: LineChart, limits: ArrayList<Float>) {
        for (limit in limits) {
            chart.axisLeft.addLimitLine(LimitLine(limit))
        }
    }
}