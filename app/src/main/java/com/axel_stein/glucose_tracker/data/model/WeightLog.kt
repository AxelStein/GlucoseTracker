package com.axel_stein.glucose_tracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(tableName = "weight_log")
data class WeightLog(
    var kg: Float = 0f,
    var pounds: Float = 0f,

    @ColumnInfo(name = "date_time")
    var dateTime: DateTime
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}