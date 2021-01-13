package com.axel_stein.glucose_tracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_list")
data class Medication(
    var title: String,
    var amount: Float = 0f,

    @ColumnInfo(name = "dosage_units")
    var dosageUnits: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}