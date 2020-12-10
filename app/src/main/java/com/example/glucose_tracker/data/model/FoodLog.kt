package com.example.glucose_tracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.LocalDate
import org.joda.time.LocalTime

@Entity(tableName = "food_log")
data class FoodLog(
    @PrimaryKey
    val id: Long,

    @ColumnInfo(name = "food_id")
    val foodId: Long,

    val date: LocalDate,
    val time: LocalTime
)