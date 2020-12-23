package com.axel_stein.glucose_tracker.ui.log_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.ui.edit_a1c.EditA1cActivity
import com.axel_stein.glucose_tracker.ui.edit_glucose.EditGlucoseActivity
import com.axel_stein.glucose_tracker.ui.edit_note.EdiNoteActivity
import com.axel_stein.glucose_tracker.utils.setShown

class LogListFragment: Fragment() {
    private val model: LogListViewModel by viewModels()
    private lateinit var adapter: LogListAdapter
    private lateinit var textEmpty: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_log_list, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        textEmpty = root.findViewById(R.id.text_empty)

        adapter = LogListAdapter(recyclerView)
        adapter.setOnItemClickListener { _, item ->
            when (item.itemType) {
                0 -> EditGlucoseActivity.launch(requireContext(), item)
                1 -> EdiNoteActivity.launch(requireContext(), item)
                2 -> EditA1cActivity.launch(requireContext(), item)
            }
        }
        recyclerView.adapter = adapter
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.getItems().observe(viewLifecycleOwner, {
            adapter.submitList(it)
            textEmpty.setShown(it.isEmpty())
        })
    }
}