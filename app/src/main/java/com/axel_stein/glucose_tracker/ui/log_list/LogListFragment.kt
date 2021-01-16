package com.axel_stein.glucose_tracker.ui.log_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.axel_stein.glucose_tracker.MainNavDirections.Companion.actionEditA1c
import com.axel_stein.glucose_tracker.MainNavDirections.Companion.actionEditGlucose
import com.axel_stein.glucose_tracker.MainNavDirections.Companion.actionEditInsulinLog
import com.axel_stein.glucose_tracker.MainNavDirections.Companion.actionEditMedicationLog
import com.axel_stein.glucose_tracker.MainNavDirections.Companion.actionEditNote
import com.axel_stein.glucose_tracker.MainNavDirections.Companion.actionEditWeightLog
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.databinding.FragmentLogListBinding
import com.axel_stein.glucose_tracker.ui.log_list.log_items.ItemType.*
import com.axel_stein.glucose_tracker.utils.ui.LinearLayoutManagerWrapper
import com.axel_stein.glucose_tracker.utils.ui.setShown

open class LogListFragment: Fragment() {
    protected val viewModel: LogListViewModel by viewModels { LogListViewModelFactory(requireActivity().application) }
    private lateinit var adapter: LogListAdapter
    private val headerDecor = TextHeaderDecor(R.layout.item_date)
    private var _binding: FragmentLogListBinding? = null
    private val binding get() = _binding!!
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLogListBinding.inflate(inflater, container, false)
        setupRecyclerView(binding.recyclerView)
        return binding.root
    }

    protected fun setupRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        recyclerView.layoutManager = LinearLayoutManagerWrapper(requireContext(), VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(headerDecor)

        adapter = LogListAdapter()
        adapter.setOnItemClickListener { _, item ->
            val id = item.id()
            findNavController().navigate(
                when (item.type()) {
                    GLUCOSE -> actionEditGlucose(id)
                    NOTE -> actionEditNote(id)
                    A1C -> actionEditA1c(id)
                    INSULIN -> actionEditInsulinLog(id)
                    MEDICATION -> actionEditMedicationLog(id)
                    WEIGHT -> actionEditWeightLog(id)
                }
            )
        }
        recyclerView.adapter = adapter
    }

    protected open fun setRecyclerViewBottomPadding(paddingBottom: Int) {
        binding.recyclerView.setPadding(0, 0, 0, paddingBottom)
    }

    protected open fun textEmpty() = binding.textEmpty

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.itemsLiveData().observe(viewLifecycleOwner, { list ->
            adapter.submitList(list)
            textEmpty().setShown(list.isEmpty())
        })
        viewModel.headersLiveData().observe(viewLifecycleOwner, {
            headerDecor.setHeaders(it)
            recyclerView?.let { rv ->
                rv.post { rv.invalidateItemDecorations() }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        recyclerView = null
    }
}