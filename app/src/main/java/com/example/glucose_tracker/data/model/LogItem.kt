package com.example.glucose_tracker.data.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

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
        val date: String,
        val time: String,
)