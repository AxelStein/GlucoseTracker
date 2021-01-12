package com.axel_stein.glucose_tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_list")
data class Medication(
    val title: String
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}