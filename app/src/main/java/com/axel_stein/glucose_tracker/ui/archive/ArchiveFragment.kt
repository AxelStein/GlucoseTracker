package com.axel_stein.glucose_tracker.ui.archive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.databinding.FragmentArchiveBinding
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.ui.log_list.LogListViewHelper
import com.axel_stein.glucose_tracker.utils.ui.setSpinnerItems
import com.axel_stein.glucose_tracker.utils.ui.setSpinnerSelection
import com.axel_stein.glucose_tracker.utils.ui.setViewVisible
import com.axel_stein.glucose_tracker.utils.ui.setupSpinner
import com.google.android.material.transition.MaterialFadeThrough
import javax.inject.Inject


class ArchiveFragment: Fragment() {
    private val archiveViewModel: ArchiveViewModel by viewModels()
    private lateinit var resources: AppResources

    private var _binding: FragmentArchiveBinding? = null
    private val binding get() = _binding!!

    private var _viewHelper: LogListViewHelper? = null
    private val viewHelper get() = _viewHelper!!

    init {
        App.appComponent.inject(this)
    }

    @Inject
    fun setResources(r: AppResources) {
        resources = r
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialFadeThrough()
        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentArchiveBinding.inflate(inflater, container, false)
        _viewHelper = LogListViewHelper(
            binding.archiveRecyclerView,
            binding.archiveTextEmpty,
            findNavController()
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        setupYearSection()
        setupMonthSection()
        archiveViewModel.logListLiveData.observe(viewLifecycleOwner, {
            viewHelper.submitLogList(it)
            (view.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }
        })
    }

    private fun setupYearSection() {
        binding.spinnerYear.setupSpinner(binding.inputLayoutYear) { position ->
            archiveViewModel.setCurrentYear(position)
        }

        archiveViewModel.yearsLiveData.observe(viewLifecycleOwner, { years ->
            binding.spinnerYear.setSpinnerItems(years)

            val isEmpty = years.isEmpty()
            setViewVisible(!isEmpty, binding.inputLayoutYear, binding.inputLayoutMonth)
            setViewVisible(isEmpty, binding.archiveTextEmpty)
        })

        archiveViewModel.selectedYearLiveData.observe(viewLifecycleOwner, { position ->
            binding.spinnerYear.setSpinnerSelection(position)
        })
    }

    private fun setupMonthSection() {
        binding.spinnerMonth.setupSpinner(binding.inputLayoutMonth) { position ->
            archiveViewModel.setCurrentMonth(position)
        }

        archiveViewModel.monthsLiveData.observe(viewLifecycleOwner, { months ->
            val titles = resources.monthsArray
            binding.spinnerMonth.setSpinnerItems(months.map { titles[it-1] })
        })
        archiveViewModel.selectedMonthLiveData.observe(viewLifecycleOwner, { position ->
            binding.spinnerMonth.setSpinnerSelection(position)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _viewHelper = null
    }
}