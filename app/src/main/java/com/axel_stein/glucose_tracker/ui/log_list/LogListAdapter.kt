package com.axel_stein.glucose_tracker.ui.log_list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.model.LogItem
import com.axel_stein.glucose_tracker.databinding.ItemA1cBinding
import com.axel_stein.glucose_tracker.databinding.ItemGlucoseBinding
import com.axel_stein.glucose_tracker.databinding.ItemNoteBinding
import com.axel_stein.glucose_tracker.ui.OnItemClickListener
import com.axel_stein.glucose_tracker.utils.CompareBuilder
import com.axel_stein.glucose_tracker.utils.inflate

class LogListAdapter : ListAdapter<LogItem, LogListAdapter.ViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<LogItem>() {
        override fun areItemsTheSame(oldItem: LogItem, newItem: LogItem): Boolean {
            return oldItem.id == newItem.id && oldItem.itemType == oldItem.itemType
        }

        override fun areContentsTheSame(oldItem: LogItem, newItem: LogItem): Boolean {
            return CompareBuilder().append(oldItem.id, newItem.id)
                .append(oldItem.itemType, newItem.itemType)
                .append(oldItem.valueMmol, newItem.valueMg)
                .append(oldItem.valueMg, newItem.valueMg)
                .append(oldItem.measured, newItem.measured)
                .append(oldItem.note, newItem.note)
                .append(oldItem.a1c, newItem.a1c)
                .append(oldItem.foods, newItem.foods)
                .append(oldItem.dateTime, newItem.dateTime)
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

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.itemType ?: -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vh = when(viewType) {
            0 -> GlucoseViewHolder(parent)
            1 -> NoteViewHolder(parent)
            2 -> A1cViewHolder(parent)
            else -> TODO()
        }
        vh.container?.setOnClickListener {
            val pos = vh.adapterPosition
            getItem(pos)?.let { item -> onItemCLickListener?.onItemClick(pos, item) }
        }
        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    abstract class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val container: View? = itemView.findViewById(R.id.container)
        abstract fun bind(item: LogItem)
    }

    class GlucoseViewHolder(parent: ViewGroup) : ViewHolder(parent.inflate(R.layout.item_glucose)) {
        private val binding = ItemGlucoseBinding.bind(itemView)
        private val measuredArr = itemView.resources.getStringArray(R.array.measured)

        override fun bind(item: LogItem) {
            binding.textValue.text = if (item.useMmol) item.valueMmol else item.valueMg
            binding.textTime.text = item.timeFormatted
            binding.textMeasured.text = measuredArr[item.measured ?: 0]
        }
    }

    class NoteViewHolder(parent: ViewGroup) : ViewHolder(parent.inflate(R.layout.item_note)) {
        private val binding = ItemNoteBinding.bind(itemView)

        override fun bind(item: LogItem) {
            binding.textNote.text = item.note
            binding.textTime.text = item.timeFormatted
        }
    }

    class A1cViewHolder(parent: ViewGroup) : ViewHolder(parent.inflate(R.layout.item_a1c)) {
        private val binding = ItemA1cBinding.bind(itemView)

        override fun bind(item: LogItem) {
            binding.textValue.text = item.a1c
            binding.textTime.text = item.timeFormatted
        }
    }
}