package com.example.glucose_tracker.data.backup

import com.example.glucose_tracker.data.model.GlucoseLog
import com.example.glucose_tracker.data.model.NoteLog

data class Backup(
    val version: Int,
    val glucoseLogs: List<GlucoseLog>,
    val noteLogs: List<NoteLog>,
    val glucoseUnits: String
)