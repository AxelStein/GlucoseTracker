package com.axel_stein.glucose_tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "insulin_list")
data class Insulin(
    val title: String,
    val type: Int = 0,  // rapid, short, intermediate, long
    val onset: Int = -1,  // min
    val peak: Int = -1,  // min
    val duration: Int = -1  // min
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}