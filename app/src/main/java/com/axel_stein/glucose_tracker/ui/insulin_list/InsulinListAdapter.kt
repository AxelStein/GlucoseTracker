package com.axel_stein.glucose_tracker.ui.insulin_list

import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.model.Insulin
import com.axel_stein.glucose_tracker.databinding.ItemInsulinBinding
import com.axel_stein.glucose_tracker.utils.CompareBuilder
import com.axel_stein.glucose_tracker.utils.inflate

class InsulinListAdapter : ListAdapter<Insulin, InsulinListAdapter.ViewHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<Insulin>() {
        override fun areItemsTheSame(oldItem: Insulin, newItem: Insulin): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Insulin, newItem: Insulin): Boolean {
            return CompareBuilder().append(oldItem.id, newItem.id)
                .append(oldItem.title, newItem.title)
                .areEqual()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }

    class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_insulin)) {
        private val binding = ItemInsulinBinding.bind(itemView)

        init {
            binding.menu.setOnClickListener {
                val menu = PopupMenu(it.context, it, Gravity.TOP, 0, R.style.PopupMenu)
                menu.inflate(R.menu.menu_insulin_item)
                menu.show()
            }
        }

        fun setItem(item: Insulin) {
            binding.title.text = item.title
        }
    }
}