package com.axel_stein.glucose_tracker.ui.archive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.databinding.FragmentArchiveBinding
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.ui.log_list.LogListFragment
import com.axel_stein.glucose_tracker.utils.CArrayAdapter
import com.axel_stein.glucose_tracker.utils.hide
import com.axel_stein.glucose_tracker.utils.show
import com.google.android.material.textfield.TextInputLayout
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
        setupSpinner(binding.spinnerYear, binding.inputLayoutYear) { position ->
            archiveViewModel.setCurrentYear(position)
        }

        archiveViewModel.yearsLiveData().observe(viewLifecycleOwner, { years ->
            setSpinnerItems(binding.spinnerYear, binding.inputLayoutYear, years)
        })

        archiveViewModel.selectedYearLiveData().observe(viewLifecycleOwner, { position ->
            setSpinnerSelection(binding.spinnerYear, position)
        })
    }

    private fun setupMonthSection() {
        setupSpinner(binding.spinnerMonth, binding.inputLayoutMonth) { position ->
            archiveViewModel.setCurrentMonth(position)
        }

        archiveViewModel.monthsLiveData().observe(viewLifecycleOwner, { months ->
            val titles = appResources.monthsArray()
            setSpinnerItems(binding.spinnerMonth, binding.inputLayoutMonth,
                months.map { titles[it-1] }
            )
        })
        archiveViewModel.selectedMonthLiveData().observe(viewLifecycleOwner, { position ->
            setSpinnerSelection(binding.spinnerMonth, position)
        })
    }

    private fun setupSpinner(spinner: AutoCompleteTextView, inputLayout: TextInputLayout, onItemClick: (position: Int) -> Unit) {
        spinner.inputType = 0  // disable ime input
        spinner.setOnKeyListener { _, _, _ -> true }  // disable hardware keyboard input
        spinner.setOnItemClickListener { _, _, position, _ ->
            inputLayout.clearFocus()
            onItemClick(position)
        }
        spinner.setOnDismissListener {
            inputLayout.clearFocus()
        }
    }

    private fun setSpinnerItems(spinner: AutoCompleteTextView, inputLayout: TextInputLayout, items: List<String>) {
        if (items.isEmpty()) {
            textEmpty().show()
            inputLayout.hide()
        } else {
            textEmpty().hide()
            inputLayout.show()
            spinner.setAdapter(
                CArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, items.toTypedArray())
            )
        }
    }

    private fun setSpinnerSelection(spinner: AutoCompleteTextView, position: Int) {
        if (position != -1) {
            spinner.listSelection = position
            val item = spinner.adapter.getItem(position)
            if (item != null) {
                spinner.setText(item as String, false)
            }
        }
    }

    override fun textEmpty() = binding.textEmpty

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}