package com.example.glucose_tracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(tableName = "note_log")
data class NoteLog(
    val note: String,

    @ColumnInfo(name = "date_time")
    val dateTime: DateTime
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}