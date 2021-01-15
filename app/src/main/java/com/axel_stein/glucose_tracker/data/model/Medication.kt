package com.axel_stein.glucose_tracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_list")
data class Medication(
    val title: String,

    @ColumnInfo(name = "dosage_form", defaultValue = "0")
    val dosageForm: Int = 0,

    @ColumnInfo(defaultValue = "0")
    var dosage: Float = 0f,

    @ColumnInfo(name = "dosage_unit", defaultValue = "-1")
    var dosageUnit: Int = -1,

    @ColumnInfo(defaultValue = "1")
    val active: Boolean = true
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}