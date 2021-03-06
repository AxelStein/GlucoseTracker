package com.axel_stein.glucose_tracker.ui.list.medication_list

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.room.model.Medication
import com.axel_stein.glucose_tracker.databinding.ItemMedicationBinding
import com.axel_stein.glucose_tracker.utils.CompareBuilder
import com.axel_stein.glucose_tracker.utils.formatRoundIfInt
import com.axel_stein.glucose_tracker.utils.ui.OnItemClickListener
import com.axel_stein.glucose_tracker.utils.ui.inflate

class MedicationListAdapter : ListAdapter<Medication, MedicationListAdapter.ViewHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<Medication>() {
        override fun areItemsTheSame(oldItem: Medication, newItem: Medication): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Medication, newItem: Medication): Boolean {
            return CompareBuilder().append(oldItem.id, newItem.id)
                .append(oldItem.title, newItem.title)
                .append(oldItem.dosage, newItem.dosage)
                .append(oldItem.dosageForm, newItem.dosageForm)
                .append(oldItem.dosageUnit, newItem.dosageUnit)
                .append(oldItem.active, newItem.active)
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
            binding.title.text = item.title
            if (item.dosage > 0f) {
                binding.dosage.text = "${item.dosage.formatRoundIfInt()} ${dosageUnits[item.dosageUnit]}"
            } else {
                binding.dosage.text = ""
            }
            binding.description.text = dosageForms[item.dosageForm]
        }
    }
}