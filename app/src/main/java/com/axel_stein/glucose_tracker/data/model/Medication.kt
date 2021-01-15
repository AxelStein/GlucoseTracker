package com.axel_stein.glucose_tracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_list")
data class Medication(
    var title: String,

    @ColumnInfo(name = "dosage_form")
    var dosageForm: Int = 0,

    var dosage: Float = 0f,

    @ColumnInfo(name = "dosage_unit")
    var dosageUnit: Int = -1,

    var active: Boolean = true
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}