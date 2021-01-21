package com.axel_stein.glucose_tracker.data.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(
    tableName = "insulin_log",
    foreignKeys = [
        ForeignKey(
            entity = Insulin::class,
            parentColumns = ["id"],
            childColumns = ["insulin_id"],
            onDelete = CASCADE
        )
    ]
)
data class InsulinLog(
    @ColumnInfo(name = "insulin_id", index = true)
    val insulinId: Long,

    val units: Float,

    val measured: Int,

    @ColumnInfo(name = "date_time")
    val dateTime: DateTime
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}