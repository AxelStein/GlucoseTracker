package com.axel_stein.glucose_tracker.data.backup

import com.axel_stein.glucose_tracker.data.room.model.*

data class Backup(
    val version: Int,
    val glucoseLogs: List<GlucoseLog>,
    val noteLogs: List<NoteLog>,
    val a1cLogs: List<A1cLog>,
    val glucoseUnits: String,
    val height: String,
    val medications: List<Medication>,
    val medicationLogs: List<MedicationLog>,
    val insulinList: List<Insulin>,
    val insulinLogs: List<InsulinLog>,
    val weightLogs: List<WeightLog>,
)