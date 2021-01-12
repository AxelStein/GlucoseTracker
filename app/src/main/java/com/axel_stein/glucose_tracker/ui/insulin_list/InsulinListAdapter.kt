package com.axel_stein.glucose_tracker.ui.insulin_list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.model.Insulin
import com.axel_stein.glucose_tracker.databinding.ItemInsulinBinding
import com.axel_stein.glucose_tracker.ui.OnItemClickListener
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
                .append(oldItem.type, newItem.type)
                .areEqual()
        }
    }

    private var onItemClickListener: OnItemClickListener<Insulin>? = null

    fun setOnItemClickListener(listener: (pos: Int, item: Insulin) -> Unit) {
        onItemClickListener = object : OnItemClickListener<Insulin> {
            override fun onItemClick(pos: Int, item: Insulin) {
                listener(pos, item)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vh = ViewHolder(parent)
        vh.itemView.setOnClickListener {
            val pos = vh.adapterPosition
            onItemClickListener?.onItemClick(pos, getItem(pos))
        }
        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }

    class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_insulin)) {
        private val binding = ItemInsulinBinding.bind(itemView)
        private val types = itemView.resources.getStringArray(R.array.insulin_types)

        fun setItem(item: Insulin) {
            binding.title.text = item.title
            binding.type.text = types[item.type]
        }
    }
}