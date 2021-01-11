package com.axel_stein.glucose_tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "insulin_list")
data class Insulin(
    val title: String,
    val type: Int = 0,
    val onset: Int = 0,
    val peak: Int = 0,
    val action: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}