package com.axel_stein.glucose_tracker.data.room.model

import androidx.room.Embedded
import androidx.room.Relation

data class InsulinLogEmbedded(
    @Embedded
    val log: InsulinLog,

    @Relation(
        parentColumn = "insulin_id",
        entityColumn = "id"
    )
    val insulin: Insulin
)