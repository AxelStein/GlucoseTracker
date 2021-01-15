package com.axel_stein.glucose_tracker.ui.log_list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.databinding.ItemLogBinding
import com.axel_stein.glucose_tracker.ui.log_list.log_items.LogItem
import com.axel_stein.glucose_tracker.utils.CompareBuilder
import com.axel_stein.glucose_tracker.utils.ui.OnItemClickListener
import com.axel_stein.glucose_tracker.utils.ui.inflate
import com.axel_stein.glucose_tracker.utils.ui.setShown

class LogListAdapter : ListAdapter<LogItem, LogListAdapter.ViewHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<LogItem>() {
        override fun areItemsTheSame(a: LogItem, b: LogItem): Boolean {
            return a.id() == b.id() && a.icon() == a.icon()
        }

        override fun areContentsTheSame(a: LogItem, b: LogItem): Boolean {
            return CompareBuilder().append(a.id(), b.id())
                .append(a.icon(), b.icon())
                .append(a.type(), b.type())
                .append(a.title(), b.title())
                .append(a.description(), b.description())
                .append(a.time(), b.time())
                .append(a.timeDescription(), b.timeDescription())
                .areEqual()
        }
    }

    private var onItemCLickListener: OnItemClickListener<LogItem>? = null

    fun setOnItemClickListener(l: (pos: Int, item: LogItem) -> Unit) {
        onItemCLickListener = object : OnItemClickListener<LogItem> {
            override fun onItemClick(pos: Int, item: LogItem) {
                l(pos, item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent).also { vh ->
            vh.setOnClickListener { pos ->
                onItemCLickListener?.onItemClick(pos, getItem(pos))
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }

    class ViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(parent.inflate(R.layout.item_log)) {
        private val binding = ItemLogBinding.bind(itemView)

        fun setOnClickListener(l: (pos: Int) -> Unit) {
            binding.container.setOnClickListener {
                l(adapterPosition)
            }
        }

        fun setItem(item: LogItem) {
            binding.icon.setImageResource(item.icon())
            binding.title.text = item.title()

            binding.description.text = item.description()
            binding.description.setShown(!item.description().isNullOrEmpty())

            binding.time.text = item.time()
            binding.timeDescription.text = item.timeDescription()
            binding.timeDescription.setShown(!item.timeDescription().isNullOrEmpty())
        }
    }
}