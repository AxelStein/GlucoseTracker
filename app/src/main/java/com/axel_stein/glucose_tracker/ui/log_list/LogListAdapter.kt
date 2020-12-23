package com.axel_stein.glucose_tracker.ui.log_list

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
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.model.LogItem
import com.axel_stein.glucose_tracker.ui.OnItemClickListener
import com.axel_stein.glucose_tracker.utils.CompareBuilder
import com.axel_stein.glucose_tracker.utils.formatDate
import com.axel_stein.glucose_tracker.utils.formatTime
import org.joda.time.LocalDate

class LogListAdapter(private val recyclerView: RecyclerView) : PagedListAdapter<LogItem, LogListAdapter.ViewHolder>(Companion), HeaderDecor.HeaderAdapter {

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
    private val headers = SparseArray<String>()
    private val headerDecor = HeaderDecor(this)

    init {
        recyclerView.addItemDecoration(headerDecor)
    }

    fun setOnItemClickListener(l: (pos: Int, item: LogItem) -> Unit) {
        onItemCLickListener = object : OnItemClickListener<LogItem> {
            override fun onItemClick(pos: Int, item: LogItem) {
                l(pos, item)
            }
        }
    }

    override fun submitList(list: PagedList<LogItem>?) {
        headerDecor.invalidate()
        headers.clear()

        var date: LocalDate? = null
        list?.forEachIndexed { index, item ->
            val itemDate = item.dateTime.toLocalDate()
            if (date == null || date != itemDate) {
                headers[index] = formatDate(recyclerView.context, item.dateTime)
                date = itemDate
            }
            item.timeFormatted = formatTime(recyclerView.context, item.dateTime)
        }

        super.submitList(list)
    }

    override fun onCurrentListChanged(previousList: PagedList<LogItem>?, currentList: PagedList<LogItem>?) {
        super.onCurrentListChanged(previousList, currentList)
        recyclerView.postDelayed({ recyclerView.invalidateItemDecorations() }, 100)
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

    class FoodsViewHolder(parent: ViewGroup) : ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_foods, parent, false)
    ) {
        private val textFoods = itemView.findViewById<TextView>(R.id.text_foods)
        private val textTime = itemView.findViewById<TextView>(R.id.text_time)

        override fun bind(item: LogItem) {
            textFoods.text = item.foods
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