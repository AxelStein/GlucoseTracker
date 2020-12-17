package com.example.glucose_tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_list")
data class FoodList(
    val title: String
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}