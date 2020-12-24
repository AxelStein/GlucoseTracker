package com.axel_stein.glucose_tracker.data.stats

import androidx.room.Ignore

data class Stats(
    val min_mmol: String,
    val min_mg: String,
    val max_mmol: String,
    val max_mg: String,
    val avg_mmol: String,
    val avg_mg: String
) {
    @Ignore
    var minFormatted = ""

    @Ignore
    var maxFormatted = ""

    @Ignore
    var avgFormatted = ""

    @Ignore
    var a1cFormatted = ""
}