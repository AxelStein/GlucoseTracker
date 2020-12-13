package com.example.glucose_tracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime

data class LogItem(
        @PrimaryKey
        val id: Long,

        @ColumnInfo(name = "item_type")
        val itemType: Int,

        @ColumnInfo(name = "value_mmol")
        var valueMmol: String?,

        @ColumnInfo(name = "value_mg")
        var valueMg: String?,

        val measured: Int?,
        val note: String?,
        val foods: String?,
        val date: LocalDate,
        val time: LocalTime,
) {
        @Ignore
        var dateTime: DateTime = DateTime().withDate(date).withTime(time)

        @Ignore
        var timeFormatted: String? = null

        init {
            valueMmol += " mmol/L"
        }
}