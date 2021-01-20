package com.axel_stein.glucose_tracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(
    tableName = "medication_log",
    foreignKeys = [
        ForeignKey(
            entity = Medication::class,
            parentColumns = ["id"],
            childColumns = ["medication_id"],
            onDelete = CASCADE
        )
    ]
)
data class MedicationLog(
    @ColumnInfo(name = "medication_id", index = true)
    val medicationId: Long,

    val amount: Float,

    val measured: Int,

    @ColumnInfo(name = "date_time")
    val dateTime: DateTime
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}
