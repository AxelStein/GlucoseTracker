package com.axel_stein.glucose_tracker.ui.log_list

import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.util.set
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.model.LogItem
import com.axel_stein.glucose_tracker.ui.edit_a1c.EditA1cActivity
import com.axel_stein.glucose_tracker.ui.edit_glucose.EditGlucoseActivity
import com.axel_stein.glucose_tracker.ui.edit_note.EditNoteActivity
import com.axel_stein.glucose_tracker.utils.formatDate
import com.axel_stein.glucose_tracker.utils.formatTime
import com.axel_stein.glucose_tracker.utils.setShown
import org.joda.time.LocalDate

open class LogListFragment: Fragment() {
    protected var layoutResourceId = R.layout.fragment_log_list
    protected val viewModel: LogListViewModel by viewModels()
    private lateinit var adapter: LogListAdapter
    private lateinit var textEmpty: TextView
    private val headerDecor = TextHeaderDecor(R.layout.item_date)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(layoutResourceId, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(headerDecor)

        textEmpty = root.findViewById(R.id.text_empty)

        adapter = LogListAdapter()
        adapter.setOnItemClickListener { _, item ->
            when (item.itemType) {
                0 -> EditGlucoseActivity.launch(requireContext(), item)
                1 -> EditNoteActivity.launch(requireContext(), item)
                2 -> EditA1cActivity.launch(requireContext(), item)
            }
        }
        recyclerView.adapter = adapter
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getItemsData().observe(viewLifecycleOwner, { list ->
            updateHeaders(list)
            adapter.submitList(list)
            textEmpty.setShown(list.isEmpty())
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
}