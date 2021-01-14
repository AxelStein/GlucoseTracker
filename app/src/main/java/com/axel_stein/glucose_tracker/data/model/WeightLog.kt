package com.axel_stein.glucose_tracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import org.joda.time.DateTime

@Entity(tableName = "weight_log")
data class WeightLog(
    var valueKg: Int = 0,
    var valuePounds: Int = 0,

    @ColumnInfo(name = "date_time")
    var dateTime: DateTime
)