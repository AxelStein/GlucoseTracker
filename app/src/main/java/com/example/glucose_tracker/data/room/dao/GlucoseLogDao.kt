package com.example.glucose_tracker.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.glucose_tracker.data.model.GlucoseLog
import io.reactivex.Completable

@Dao
interface GlucoseLogDao {

    @Insert
    fun insert(log: GlucoseLog): Completable
}