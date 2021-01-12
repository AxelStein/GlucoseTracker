package com.axel_stein.glucose_tracker.ui.archive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.databinding.FragmentArchiveBinding
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.ui.log_list.LogListFragment
import com.axel_stein.glucose_tracker.utils.setSpinnerItems
import com.axel_stein.glucose_tracker.utils.setSpinnerSelection
import com.axel_stein.glucose_tracker.utils.setViewVisible
import com.axel_stein.glucose_tracker.utils.setupSpinner
import javax.inject.Inject


class ArchiveFragment: LogListFragment() {
    private val archiveViewModel: ArchiveViewModel by viewModels()
    private var _binding: FragmentArchiveBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var appResources: AppResources

    init {
        App.appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentArchiveBinding.inflate(inflater, container, false)
        setupRecyclerView(binding.recyclerView)
        setupYearSection()
        setupMonthSection()

        archiveViewModel.loadItemsByYearMonthLiveData().observe(viewLifecycleOwner, {
            viewModel.loadItemsByYearMonth(it)
        })
        return binding.root
    }

    private fun setupYearSection() {
        binding.spinnerYear.setupSpinner(binding.inputLayoutYear) { position ->
            archiveViewModel.setCurrentYear(position)
        }

        archiveViewModel.yearsLiveData().observe(viewLifecycleOwner, { years ->
            binding.spinnerYear.setSpinnerItems(years)

            val isEmpty = years.isEmpty()
            setViewVisible(!isEmpty, binding.inputLayoutYear, binding.inputLayoutMonth)
            setViewVisible(isEmpty, textEmpty())
        })

        archiveViewModel.selectedYearLiveData().observe(viewLifecycleOwner, { position ->
            binding.spinnerYear.setSpinnerSelection(position)
        })
    }

    private fun setupMonthSection() {
        binding.spinnerMonth.setupSpinner(binding.inputLayoutMonth) { position ->
            archiveViewModel.setCurrentMonth(position)
        }

        archiveViewModel.monthsLiveData().observe(viewLifecycleOwner, { months ->
            val titles = appResources.monthsArray()
            binding.spinnerMonth.setSpinnerItems(months.map { titles[it-1] })
        })
        archiveViewModel.selectedMonthLiveData().observe(viewLifecycleOwner, { position ->
            binding.spinnerMonth.setSpinnerSelection(position)
        })
    }

    override fun textEmpty() = binding.textEmpty

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}