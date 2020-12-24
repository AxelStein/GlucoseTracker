package com.axel_stein.glucose_tracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(tableName = "food_log")
data class FoodLog(
    val id: Long?,

    @ColumnInfo(name = "food_id")
    val foodId: Long,

    val quantity: Int,

    @ColumnInfo(name = "date_time")
    val dateTime: DateTime?
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "row_id")
    var rowId = 0L
}