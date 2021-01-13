package com.axel_stein.glucose_tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_list")
data class Medication(
    var title: String,
    var dosage: Float = 0f,
    var units: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}