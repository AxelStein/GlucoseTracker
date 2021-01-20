package com.axel_stein.glucose_tracker.ui.log_list

import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.axel_stein.glucose_tracker.MainNavDirections.Companion.actionEditA1c
import com.axel_stein.glucose_tracker.MainNavDirections.Companion.actionEditApLog
import com.axel_stein.glucose_tracker.MainNavDirections.Companion.actionEditGlucose
import com.axel_stein.glucose_tracker.MainNavDirections.Companion.actionEditInsulinLog
import com.axel_stein.glucose_tracker.MainNavDirections.Companion.actionEditMedicationLog
import com.axel_stein.glucose_tracker.MainNavDirections.Companion.actionEditNote
import com.axel_stein.glucose_tracker.MainNavDirections.Companion.actionEditWeightLog
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.room.LogRepository.LogListResult
import com.axel_stein.glucose_tracker.ui.log_list.log_items.ItemType
import com.axel_stein.glucose_tracker.utils.ui.LinearLayoutManagerWrapper
import com.axel_stein.glucose_tracker.utils.ui.setShown

class LogListViewHelper(
    private val recyclerView: RecyclerView,
    private val textEmpty: TextView,
    private val navController: NavController
) {
    private val headerDecor = TextHeaderDecor(R.layout.item_date)
    private lateinit var adapter: LogListAdapter

    init {
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManagerWrapper(recyclerView.context, VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(headerDecor)

        adapter = LogListAdapter()
        adapter.setOnItemClickListener { _, item ->
            val id = item.id()
            navController.navigate(
                when (item.type()) {
                    ItemType.GLUCOSE -> actionEditGlucose(id)
                    ItemType.NOTE -> actionEditNote(id)
                    ItemType.A1C -> actionEditA1c(id)
                    ItemType.INSULIN -> actionEditInsulinLog(id)
                    ItemType.MEDICATION -> actionEditMedicationLog(id)
                    ItemType.WEIGHT -> actionEditWeightLog(id)
                    ItemType.AP -> actionEditApLog(id)
                    ItemType.PULSE -> actionEditWeightLog(id)  // todo
                }
            )
        }
        recyclerView.adapter = adapter
    }

    fun setRecyclerViewBottomPadding(paddingBottom: Int) {
        recyclerView.setPadding(0, 0, 0, paddingBottom)
    }

    fun submitLogList(result: LogListResult) {
        adapter.submitList(result.list)
        textEmpty.setShown(result.list.isEmpty())

        headerDecor.setHeaders(result.headers)
        recyclerView.let { rv ->
            rv.post { rv.invalidateItemDecorations() }
        }
    }
}