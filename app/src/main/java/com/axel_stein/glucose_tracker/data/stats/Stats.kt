package com.axel_stein.glucose_tracker.data.stats

import androidx.room.Ignore

data class Stats(
    val min_mmol: String? = null,
    val min_mg: String? = null,
    val max_mmol: String? = null,
    val max_mg: String? = null,
    val avg_mmol: String? = null,
    val avg_mg: String? = null
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