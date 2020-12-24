package com.axel_stein.glucose_tracker.ui.log_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.model.LogItem
import com.axel_stein.glucose_tracker.ui.OnItemClickListener
import com.axel_stein.glucose_tracker.utils.CompareBuilder

class LogListAdapter : PagedListAdapter<LogItem, LogListAdapter.ViewHolder>(Companion) {

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

    class GlucoseViewHolder(parent: ViewGroup) : ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_glucose, parent, false)
    ) {
        private val textValue = itemView.findViewById<TextView>(R.id.text_value)
        private val textTime = itemView.findViewById<TextView>(R.id.text_time)
        private val textMeasured = itemView.findViewById<TextView>(R.id.text_measured)
        private val measuredArr = itemView.resources.getStringArray(R.array.measured)

        override fun bind(item: LogItem) {
            textValue.text = if (item.useMmol) item.valueMmol else item.valueMg
            textTime.text = item.timeFormatted
            textMeasured.text = measuredArr[item.measured ?: 0]
        }
    }

    class NoteViewHolder(parent: ViewGroup) : ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
    ) {
        private val textNote = itemView.findViewById<TextView>(R.id.text_note)
        private val textTime = itemView.findViewById<TextView>(R.id.text_time)

        override fun bind(item: LogItem) {
            textNote.text = item.note
            textTime.text = item.timeFormatted
        }
    }

    class A1cViewHolder(parent: ViewGroup) : ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_a1c, parent, false)
    ) {
        private val textValue = itemView.findViewById<TextView>(R.id.text_value)
        private val textTime = itemView.findViewById<TextView>(R.id.text_time)

        override fun bind(item: LogItem) {
            textValue.text = item.a1c
            textTime.text = item.timeFormatted
        }
    }
}