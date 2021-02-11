package com.axel_stein.glucose_tracker.data.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(tableName = "weight_log")
data class WeightLog(
    @ColumnInfo(defaultValue = "0")
    val kg: Float = 0f,

    @ColumnInfo(defaultValue = "0")
    val pounds: Float = 0f,

    @ColumnInfo(name = "date_time")
    val dateTime: DateTime
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}