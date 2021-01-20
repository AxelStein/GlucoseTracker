package com.axel_stein.glucose_tracker.utils.ui

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class CArrayAdapter<T>(context: Context, resource: Int, val items: Array<T>) : ArrayAdapter<T>(context, resource, 0, items) {
    override fun getFilter(): Filter {
        return NoFilter(this)
    }

    private class NoFilter<T>(val adapter: CArrayAdapter<T>) : Filter() {
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