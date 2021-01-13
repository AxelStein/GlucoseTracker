package com.axel_stein.glucose_tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "insulin_list")
data class Insulin(
    var title: String,
    var type: Int = 0,  // rapid, short, intermediate, long
    var active: Boolean = true
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}