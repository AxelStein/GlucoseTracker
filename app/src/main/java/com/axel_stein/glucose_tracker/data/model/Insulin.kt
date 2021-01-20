package com.axel_stein.glucose_tracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "insulin_list")
data class Insulin(
    val title: String,

    @ColumnInfo(defaultValue = "0")
    val type: Int = 0,  // rapid, short, intermediate, long

    @ColumnInfo(defaultValue = "1")
    val active: Boolean = true
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}