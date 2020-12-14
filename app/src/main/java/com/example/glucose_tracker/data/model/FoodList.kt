package com.example.glucose_tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_list")
data class FoodList(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,

    val title: String
)