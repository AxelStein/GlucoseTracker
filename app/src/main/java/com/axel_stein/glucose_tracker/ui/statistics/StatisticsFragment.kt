package com.axel_stein.glucose_tracker.ui.statistics

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.databinding.FragmentStatisticsBinding
import com.axel_stein.glucose_tracker.ui.statistics.helpers.ChartColors
import com.axel_stein.glucose_tracker.ui.statistics.helpers.LabelValueFormatter
import com.axel_stein.glucose_tracker.utils.ui.hide
import com.axel_stein.glucose_tracker.utils.ui.setItemSelectedListener
import com.axel_stein.glucose_tracker.utils.ui.show
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.utils.Utils
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.MaterialFadeThrough

class StatisticsFragment: Fragment() {
    private lateinit var viewModel: StatisticsViewModel
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialFadeThrough()
        enterTransition = MaterialFadeThrough()
        viewModel = ViewModelProvider(this, StatisticsFactory(ChartColors(requireActivity())))
            .get(StatisticsViewModel::class.java)
        Utils.init(context)
    }

    @SuppressLint("CheckResult")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupStatsView()
        setupChart()

        binding.spinnerStats.onItemSelectedListener = setItemSelectedListener {
            viewModel.setStatsPeriod(it)
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
    }

    private fun setupStatsView() {
        viewModel.statsLiveData().observe(viewLifecycleOwner, { stats ->
            if (stats != null) {
                binding.min.text = stats.minFormatted
                binding.max.text = stats.maxFormatted
                binding.avg.text = stats.avgFormatted
                binding.a1c.text = stats.a1cFormatted
                binding.cardViewStats.show()
            } else {
                binding.cardViewStats.hide()
            }
        })

        viewModel.diabetesControlLiveData().observe(viewLifecycleOwner, {
            binding.diabetesControl.setIconStartRes(
                when (it) {
                    0 -> R.drawable.icon_checked
                    1 -> R.drawable.icon_checked
                    else -> R.drawable.icon_error
                }
            )
            binding.diabetesControl.setText(
                when (it) {
                    0 -> R.string.diabetes_control_good
                    1 -> R.string.diabetes_control_relative
                    else -> R.string.diabetes_control_bad
                }
            )
            binding.diabetesControl.setColorAttr(
                when (it) {
                    0 -> R.attr.diabetesControlGoodColor
                    1 -> R.attr.diabetesControlRelativeColor
                    else -> R.attr.diabetesControlBadColor
                }
            )
        })
    }

    private fun setupChart() {
        setupChartView(binding.chart)
        binding.chartTitleSpinner.onItemSelectedListener = setItemSelectedListener {
            viewModel.setChartType(it)
            binding.chartPeriodSpinner.visibility = if (it < 2) VISIBLE else INVISIBLE
        }
        binding.chartPeriodSpinner.onItemSelectedListener = setItemSelectedListener {
            viewModel.setChartPeriod(it)
        }

        viewModel.chartLiveData().observe(viewLifecycleOwner, {
            setChartLineData(binding.chart, it)
        })
        viewModel.chartLimits().observe(viewLifecycleOwner, {
            setChartLimitLines(binding.chart, it)
        })
        viewModel.chartLabelsLiveData().observe(viewLifecycleOwner, {
            binding.chart.xAxis.valueFormatter = LabelValueFormatter(it)
        })
    }

    private fun setupChartView(chart: LineChart) {
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.xAxis.textColor = MaterialColors.getColor(requireActivity(), R.attr.chartTextColor, Color.BLACK)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.axisLeft.spaceTop = 50f
        chart.axisLeft.axisMinimum = 0f
        chart.axisLeft.textColor = MaterialColors.getColor(requireActivity(), R.attr.chartTextColor, Color.BLACK)
        chart.axisRight.setDrawLabels(false)
        chart.isDoubleTapToZoomEnabled = false
        chart.setExtraOffsets(8f, 0f, 8f, 4f)
    }

    private fun setChartLineData(chart: LineChart, data: LineData?) {
        chart.axisLeft.removeAllLimitLines()
        chart.data = data
        chart.notifyDataSetChanged()
        if (data != null) {
            chart.xAxis.granularity = 1f
            chart.xAxis.labelCount = data.entryCount
        }
        chart.setVisibleXRange(0f, 10f)
        chart.invalidate()
    }

    private fun setChartLimitLines(chart: LineChart, limits: ArrayList<Float>) {
        for (limit in limits) {
            chart.axisLeft.addLimitLine(LimitLine(limit))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}