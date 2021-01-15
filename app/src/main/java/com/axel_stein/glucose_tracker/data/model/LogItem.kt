package com.axel_stein.glucose_tracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.joda.time.DateTime

data class LogItem(
    @PrimaryKey
    val id: Long,

    @ColumnInfo(name = "item_type")
    val itemType: Int,

    @ColumnInfo(name = "value_mmol")
    var valueMmol: String?,

    @ColumnInfo(name = "value_mg")
    var valueMg: String?,

    val kg: Float,

    @ColumnInfo(name = "insulin_id")
    val insulinId: Long,
    val units: Float,

    @ColumnInfo(name = "medication_id")
    val medicationId: Long,
    val amount: Float,

    val measured: Int?,
    val note: String?,
    var a1c: String?,
    val foods: String?,

    @ColumnInfo(name = "date_time")
    var dateTime: DateTime
) {
    @Ignore
    var timeFormatted: String? = null

    @Ignore
    var useMmol = true
}