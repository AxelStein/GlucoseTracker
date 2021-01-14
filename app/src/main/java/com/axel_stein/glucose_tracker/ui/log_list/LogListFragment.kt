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
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.databinding.FragmentLogListBinding
import com.axel_stein.glucose_tracker.ui.edit_a1c.EditA1cActivityDirections.Companion.launchEditA1c
import com.axel_stein.glucose_tracker.ui.edit_a1c.EditA1cActivityDirections.Companion.launchEditNote
import com.axel_stein.glucose_tracker.ui.edit_glucose.EditGlucoseActivityDirections.Companion.launchEditGlucose
import com.axel_stein.glucose_tracker.utils.LinearLayoutManagerWrapper
import com.axel_stein.glucose_tracker.utils.setShown

open class LogListFragment: Fragment() {
    protected val viewModel: LogListViewModel by viewModels { LogListViewModelFactory(requireActivity().application) }
    private lateinit var adapter: LogListAdapter
    private val headerDecor = TextHeaderDecor(R.layout.item_date)
    private var _binding: FragmentLogListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLogListBinding.inflate(inflater, container, false)
        setupRecyclerView(binding.recyclerView)
        return binding.root
    }

    protected fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManagerWrapper(requireContext(), VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(headerDecor)

        adapter = LogListAdapter()
        adapter.setOnItemClickListener { _, item ->
            findNavController().navigate(
                when (item.itemType) {
                    0 -> launchEditGlucose(item.id)
                    1 -> launchEditNote(item.id)
                    else -> launchEditA1c(item.id)
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
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}