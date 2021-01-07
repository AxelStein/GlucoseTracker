package com.axel_stein.glucose_tracker.ui.statistics

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.databinding.FragmentStatisticsBinding
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
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, StatisticsFactory(ChartColors(requireActivity())))
            .get(StatisticsViewModel::class.java)
    }

    @SuppressLint("CheckResult")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        setupStatsView()
        setupBeforeMealChart()
        setupAfterMealChart()
        setupA1cChart()

        binding.spinnerPeriod.onItemSelectedListener = setItemSelectedListener {
            viewModel.setPeriod(it)
        }
        viewModel.showErrorLiveData().observe(viewLifecycleOwner, {
            if (it) {
                binding.content.hide()
                binding.textError.show()
            } else {
                binding.content.show()
                binding.textError.hide()
            }
        })
        return binding.root
    }

    private fun setupStatsView() {
        viewModel.statsLiveData().observe(viewLifecycleOwner, { stats ->
            if (stats != null) {
                binding.textMin.text = stats.minFormatted
                binding.textMax.text = stats.maxFormatted
                binding.textAvg.text = stats.avgFormatted
                binding.textA1c.text = stats.a1cFormatted
                binding.cardViewStats.show()
            } else {
                binding.cardViewStats.hide()
            }
        })

        viewModel.diabetesControlLiveData().observe(viewLifecycleOwner, {
            hideView(binding.textA1cControlGood, binding.textA1cControlAvg, binding.textA1cControlBad)
            when (it) {
                0 -> binding.textA1cControlGood.show()
                1 -> binding.textA1cControlAvg.show()
                2 -> binding.textA1cControlBad.show()
            }
        })
    }

    private fun setupBeforeMealChart() {
        setupChartView(binding.beforeMealChart)

        viewModel.beforeMealChartLiveData().observe(viewLifecycleOwner, {
            setChartLineData(binding.beforeMealChart, it)
            binding.cardViewBeforeMeal.show()
        })
        viewModel.beforeMealMaxLiveData().observe(viewLifecycleOwner, {
            binding.beforeMealChart.axisLeft.axisMaximum = viewModel.axisMaximum(it)
        })
        viewModel.beforeMealLimitsLiveData().observe(viewLifecycleOwner, {
            addChartLimitLines(binding.beforeMealChart, it)
        })
        viewModel.beforeMealLabelsLiveData().observe(viewLifecycleOwner, {
            binding.beforeMealChart.xAxis.valueFormatter = LabelValueFormatter(it)
        })
    }

    private fun setupAfterMealChart() {
        setupChartView(binding.afterMealChart)

        viewModel.afterMealChartLiveData().observe(viewLifecycleOwner, {
            setChartLineData(binding.afterMealChart, it)
            binding.cardViewAfterMeal.show()
        })
        viewModel.afterMealMaxLiveData().observe(viewLifecycleOwner, {
            binding.afterMealChart.axisLeft.axisMaximum = viewModel.axisMaximum(it)
        })
        viewModel.afterMealLimitsLiveData().observe(viewLifecycleOwner, {
            addChartLimitLines(binding.afterMealChart, it)
        })
        viewModel.afterMealLabelsLiveData().observe(viewLifecycleOwner, {
            binding.afterMealChart.xAxis.valueFormatter = LabelValueFormatter(it)
        })
    }

    private fun setupA1cChart() {
        setupChartView(binding.a1cChart)
        addChartLimitLines(binding.a1cChart, arrayListOf(6f, 7f, 8f))

        viewModel.a1cChartLiveData().observe(viewLifecycleOwner, {
            setChartLineData(binding.a1cChart, it)
            binding.cardViewA1c.show()
        })
        viewModel.a1cMaxLiveData().observe(viewLifecycleOwner, {
            binding.a1cChart.axisLeft.axisMaximum = if (it < 10f) 10f else it + 2f
        })
        viewModel.a1cLabelsLiveData().observe(viewLifecycleOwner, {
            binding.a1cChart.xAxis.valueFormatter = LabelValueFormatter(it)
        })
    }

    private fun setupChartView(chart: LineChart) {
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setDrawGridBackground(false)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}