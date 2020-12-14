package com.example.glucose_tracker.ui.log_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.glucose_tracker.R
import com.example.glucose_tracker.ui.edit_glucose.EditGlucoseActivity
import com.example.glucose_tracker.utils.setShown

class LogListFragment: Fragment() {
    private lateinit var adapter: LogListAdapter
    private lateinit var textEmpty: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_log_list, container, false)
        textEmpty = root.findViewById(R.id.text_empty)

        val recyclerView = root.findViewById<RecyclerView>(R.id.recycler_view)

        adapter = LogListAdapter(recyclerView)
        adapter.setOnItemClickListener { _, item ->
            if (item.itemType == 0) {
                EditGlucoseActivity.launch(requireContext(), item)
            }
        }
        recyclerView.adapter = adapter
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model: LogListViewModel by viewModels()
        model.items.observe(viewLifecycleOwner, {
            adapter.submitList(it)
            textEmpty.setShown(it.isEmpty())
        })
    }
}