package com.example.glucose_tracker.ui.log_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.glucose_tracker.R

class LogListFragment: Fragment() {
    private lateinit var adapter: LogListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_log_list, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.recycler_view)

        adapter = LogListAdapter(recyclerView)
        adapter.setOnItemClickListener { pos, item ->
            Log.d("TAG", "$pos $item")
        }
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(HeaderDecor(adapter))
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model: LogListViewModel by viewModels()
        model.items.observe(viewLifecycleOwner, {
            Log.d("TAG", "pagedList $it")
            adapter.submitList(it)
        })
    }
}