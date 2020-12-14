package com.example.glucose_tracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(tableName = "glucose_log")
data class GlucoseLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,

    @ColumnInfo(name = "value_mmol")
    val valueMmol: Float,

    @ColumnInfo(name = "value_mg")
    val valueMg: Int,

    val measured: Int,

    @ColumnInfo(name = "date_time")
    val dateTime: DateTime
)