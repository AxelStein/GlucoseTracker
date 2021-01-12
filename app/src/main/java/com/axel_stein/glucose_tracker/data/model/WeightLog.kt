package com.axel_stein.glucose_tracker.data.model

import androidx.room.Entity

@Entity(tableName = "weight_log")
data class WeightLog(
    val valueKg: Int = 0,
    val valuePounds: Int = 0
)