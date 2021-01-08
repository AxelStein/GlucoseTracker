package com.axel_stein.glucose_tracker.ui.log_list

import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.set
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.model.LogItem
import com.axel_stein.glucose_tracker.databinding.FragmentLogListBinding
import com.axel_stein.glucose_tracker.ui.edit_a1c.EditA1cActivity
import com.axel_stein.glucose_tracker.ui.edit_glucose.EditGlucoseActivity
import com.axel_stein.glucose_tracker.ui.edit_note.EditNoteActivity
import com.axel_stein.glucose_tracker.utils.LinearLayoutManagerWrapper
import com.axel_stein.glucose_tracker.utils.formatDate
import com.axel_stein.glucose_tracker.utils.formatTime
import com.axel_stein.glucose_tracker.utils.setShown
import org.joda.time.LocalDate

open class LogListFragment: Fragment() {
    protected val viewModel: LogListViewModel by viewModels()
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
            when (item.itemType) {
                0 -> EditGlucoseActivity.launch(requireContext(), item)
                1 -> EditNoteActivity.launch(requireContext(), item)
                2 -> EditA1cActivity.launch(requireContext(), item)
            }
        }
        recyclerView.adapter = adapter
    }

    protected open fun textEmpty() = binding.textEmpty

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.itemsLiveData().observe(viewLifecycleOwner, { list ->
            updateHeaders(list)
            adapter.submitList(list)
            textEmpty().setShown(list.isEmpty())
        })
    }

    private fun updateHeaders(list: List<LogItem>?) {
        val headers = SparseArray<String>()
        var date: LocalDate? = null
        list?.forEachIndexed { index, item ->
            val itemDate = item.dateTime.toLocalDate()
            if (date == null || date != itemDate) {
                headers[index] = formatDate(requireContext(), item.dateTime)
                date = itemDate
            }
            item.timeFormatted = formatTime(requireContext(), item.dateTime)
        }
        headerDecor.setHeaders(headers)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}