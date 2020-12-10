package com.example.glucose_tracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.LocalDate
import org.joda.time.LocalTime

@Entity(tableName = "glucose_log")
data class GlucoseLog(
        @PrimaryKey
        val id: Long,

        @ColumnInfo(name = "value_mmol")
        val valueMmol: Float,

        @ColumnInfo(name = "value_mg")
        val valueMg: Float,

        val measured: Int,
        val date: LocalDate,
        val time: LocalTime
)