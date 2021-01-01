package com.axel_stein.glucose_tracker.ui.archive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.ui.log_list.LogListFragment
import com.axel_stein.glucose_tracker.utils.CArrayAdapter
import com.axel_stein.glucose_tracker.utils.hide
import com.axel_stein.glucose_tracker.utils.show
import javax.inject.Inject


class ArchiveFragment: LogListFragment() {
    private val archiveViewModel: ArchiveViewModel by viewModels()

    @Inject
    lateinit var appResources: AppResources

    init {
        App.appComponent.inject(this)
        layoutResourceId = R.layout.fragment_archive
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)

        val textEmpty = root?.findViewById<View>(R.id.text_empty)

        val inputLayoutYear = root?.findViewById<View>(R.id.input_layout_year)
        val spinnerYear = root?.findViewById<AutoCompleteTextView>(R.id.spinner_year)
        spinnerYear?.inputType = 0  // disable ime input
        spinnerYear?.setOnKeyListener { _, _, _ -> true }  // disable hardware keyboard input
        spinnerYear?.setOnItemClickListener { _, _, position, _ ->
            inputLayoutYear?.clearFocus()
            archiveViewModel.setCurrentYear(position)
        }
        spinnerYear?.setOnDismissListener {
            inputLayoutYear?.clearFocus()
        }

        archiveViewModel.yearsData().observe(viewLifecycleOwner, { years ->
            if (years.isEmpty()) {
                inputLayoutYear?.hide()
                textEmpty?.show()
            } else {
                inputLayoutYear?.show()
                textEmpty?.hide()

                val adapter = CArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, years.toTypedArray())
                spinnerYear?.setAdapter(adapter)
            }
        })

        archiveViewModel.selectedYearData().observe(viewLifecycleOwner, { position ->
            if (position != -1) {
                spinnerYear?.listSelection = position
                val item = spinnerYear?.adapter?.getItem(position)
                if (item != null) {
                    spinnerYear.setText(item as String, false)
                }
            }
        })

        val inputLayoutMonth = root?.findViewById<View>(R.id.input_layout_month)
        val spinnerMonth = root?.findViewById<AutoCompleteTextView>(R.id.spinner_month)
        spinnerMonth?.inputType = 0  // disable ime input
        spinnerMonth?.setOnKeyListener { _, _, _ -> true }  // disable hardware keyboard input
        spinnerMonth?.setOnItemClickListener { _, _, position, _ ->
            inputLayoutMonth?.clearFocus()
            archiveViewModel.setCurrentMonth(position)
        }
        spinnerMonth?.setOnDismissListener {
            inputLayoutMonth?.clearFocus()
        }

        archiveViewModel.monthsData().observe(viewLifecycleOwner, { months ->
            if (months.isEmpty()) {
                inputLayoutMonth?.hide()
            } else {
                inputLayoutMonth?.show()

                val monthTitles = appResources.monthsArray()
                val selectedMonthTitles = mutableListOf<String>()
                months.forEach {
                    selectedMonthTitles.add(monthTitles[it-1])
                }
                val adapter = CArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, selectedMonthTitles.toTypedArray())
                spinnerMonth?.setAdapter(adapter)
            }
        })
        archiveViewModel.selectedMonthData().observe(viewLifecycleOwner, { position ->
            if (position != -1) {
                spinnerMonth?.listSelection = position
                val item = spinnerMonth?.adapter?.getItem(position)
                if (item != null) {
                    spinnerMonth.setText(item as String, false)
                }
            }
        })

        archiveViewModel.loadItemsByYearMonthData().observe(viewLifecycleOwner, {
            viewModel.loadItemsByYearMonth(it)
        })
        return root
    }
}