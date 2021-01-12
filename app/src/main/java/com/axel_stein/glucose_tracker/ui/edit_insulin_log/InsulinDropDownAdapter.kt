package com.axel_stein.glucose_tracker.ui.edit_insulin_log

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.utils.inflate

class InsulinDropDownAdapter(items: List<String>) : BaseAdapter(), Filterable {
    private var items: ArrayList<Item> = ArrayList()

    init {
        for (s in items) {
            this.items.add(InsulinItem(s))
        }
        this.items.add(EditItem())
    }

    override fun getCount() = items.size

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return getItemViewType(position).toLong()
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        if (item is InsulinItem) return 0
        return 1
    }

    override fun getViewTypeCount() = 2

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val item = getItem(position)
        val itemType = getItemViewType(position)
        val view = convertView ?: parent?.inflate(
            when (itemType) {
                0 -> R.layout.item_popup
                else -> R.layout.item_manage_insulin
            }
        )
        if (itemType == 0) {
            (view as TextView).text = (item as InsulinItem).data
        }
        return view
    }

    private interface Item

    private data class InsulinItem(
        val data: String
    ) : Item {
        override fun toString(): String {
            return data
        }
    }

    private class EditItem : Item

    override fun getFilter(): Filter {
        return NoFilter(this)
    }

    private class NoFilter(val adapter: InsulinDropDownAdapter) : Filter() {
        override fun performFiltering(arg0: CharSequence): FilterResults {
            val result = FilterResults()
            result.values = adapter.items
            result.count = adapter.items.size
            return result
        }

        override fun publishResults(arg0: CharSequence, arg1: FilterResults) {
            adapter.notifyDataSetChanged()
        }
    }
}