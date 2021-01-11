package com.axel_stein.glucose_tracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(tableName = "insulin_log")
data class InsulinLog(
    @ColumnInfo(name = "insulin_id")
    val insulinId: Long,

    val units: Int,

    val measured: Int,

    @ColumnInfo(name = "date_time")
    val dateTime: DateTime
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}