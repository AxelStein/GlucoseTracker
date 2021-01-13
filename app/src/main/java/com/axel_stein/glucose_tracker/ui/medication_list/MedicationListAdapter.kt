package com.axel_stein.glucose_tracker.ui.medication_list

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.model.Medication
import com.axel_stein.glucose_tracker.databinding.ItemMedicationBinding
import com.axel_stein.glucose_tracker.ui.OnItemClickListener
import com.axel_stein.glucose_tracker.utils.CompareBuilder
import com.axel_stein.glucose_tracker.utils.formatIfInt
import com.axel_stein.glucose_tracker.utils.inflate

class MedicationListAdapter : ListAdapter<Medication, MedicationListAdapter.ViewHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<Medication>() {
        override fun areItemsTheSame(oldItem: Medication, newItem: Medication): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Medication, newItem: Medication): Boolean {
            return CompareBuilder().append(oldItem.id, newItem.id)
                .append(oldItem.title, newItem.title)
                .append(oldItem.dosage, oldItem.dosage)
                .append(oldItem.dosageUnit, oldItem.dosageUnit)
                .areEqual()
        }
    }

    private var onItemClickListener: OnItemClickListener<Medication>? = null

    fun setOnItemClickListener(listener: (pos: Int, item: Medication) -> Unit) {
        onItemClickListener = object : OnItemClickListener<Medication> {
            override fun onItemClick(pos: Int, item: Medication) {
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

    class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_medication)) {
        private val binding = ItemMedicationBinding.bind(itemView)
        private val dosageForms = itemView.resources.getStringArray(R.array.dosage_forms)
        private val dosageUnits = itemView.resources.getStringArray(R.array.dosage_units)

        @SuppressLint("SetTextI18n")
        fun setItem(item: Medication) {
            if (item.dosageUnit >= 0) {
                binding.title.text = "${item.title} (${item.dosage.formatIfInt()} ${dosageUnits[item.dosageUnit]})"
            } else {
                binding.title.text = item.title
            }
            binding.description.text = dosageForms[item.dosageForm]
        }
    }
}