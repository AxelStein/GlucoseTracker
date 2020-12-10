package com.example.glucose_tracker.ui.log_list

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.util.set
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.glucose_tracker.R
import com.example.glucose_tracker.data.model.LogItem
import com.example.glucose_tracker.ui.OnItemClickListener
import com.example.glucose_tracker.utils.formatDate
import org.joda.time.LocalDate

class LogListAdapter(private val recyclerView: RecyclerView) : PagedListAdapter<LogItem, LogListAdapter.ViewHolder>(object : DiffUtil.ItemCallback<LogItem>() {
    override fun areItemsTheSame(oldItem: LogItem, newItem: LogItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: LogItem, newItem: LogItem): Boolean {
        return oldItem == newItem
    }
}), HeaderDecor.HeaderAdapter {

    private var onItemCLickListener: OnItemClickListener<LogItem>? = null
    private val headers = SparseArray<String>()

    fun setOnItemClickListener(l: (pos: Int, item: LogItem) -> Unit) {
        onItemCLickListener = object : OnItemClickListener<LogItem> {
            override fun onItemClick(pos: Int, item: LogItem) {
                l(pos, item)
            }
        }
    }

    override fun submitList(pagedList: PagedList<LogItem>?) {
        super.submitList(pagedList)

        var date = ""
        pagedList?.forEachIndexed { index, item ->
            if (date.isEmpty() || item.date != date) {
                headers[index] = formatDate(recyclerView.context, LocalDate(item.date))
                date = item.date
            }
        }
    }

    override fun hasHeader(position: Int): Boolean = headers.indexOfKey(position) >= 0

    override fun getHeaderView(position: Int): View {
        val view = LayoutInflater.from(recyclerView.context).inflate(R.layout.item_date, recyclerView, false) as TextView
        view.text = headers[position]
        return view
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.itemType ?: -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vh = when(viewType) {
            0 -> GlucoseViewHolder(parent)
            1 -> NoteViewHolder(parent)
            2 -> FoodsViewHolder(parent)
            else -> DateViewHolder(parent)
        }
        if (vh !is DateViewHolder) {
            vh.itemView.setOnClickListener {
                val pos = vh.adapterPosition
                getItem(pos)?.let { item -> onItemCLickListener?.onItemClick(pos, item) }
            }
        }
        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    abstract class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
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
            textValue.text = item.valueMmol
            textTime.text = item.time
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
            textTime.text = item.time
        }
    }

    class FoodsViewHolder(parent: ViewGroup) : ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_foods, parent, false)
    ) {
        private val textFoods = itemView.findViewById<TextView>(R.id.text_foods)
        private val textTime = itemView.findViewById<TextView>(R.id.text_time)

        override fun bind(item: LogItem) {
            textFoods.text = item.foods
            textTime.text = item.time
        }
    }

    class DateViewHolder(parent: ViewGroup) : ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_date, parent, false)
    ) {
        override fun bind(item: LogItem) {
            (itemView as TextView).text = item.date
        }
    }
}