package com.example.glucose_tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.LocalDate
import org.joda.time.LocalTime

@Entity(tableName = "note_log")
data class NoteLog(
    @PrimaryKey
    val id: Long,
    val note: String,
    val date: LocalDate? = null,
    val time: LocalTime? = null
)