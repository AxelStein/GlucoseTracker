package com.axel_stein.glucose_tracker.data.room.model

import androidx.room.Embedded
import androidx.room.Relation

data class MedicationLogEmbedded(
    @Embedded
    val log: MedicationLog,

    @Relation(
        parentColumn = "medication_id",
        entityColumn = "id"
    )
    val medication: Medication
)