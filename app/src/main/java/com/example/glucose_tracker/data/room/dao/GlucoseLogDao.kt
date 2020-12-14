package com.example.glucose_tracker.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.glucose_tracker.data.model.GlucoseLog
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface GlucoseLogDao {
    @Insert
    fun insert(log: GlucoseLog): Completable

    @Query("select * from glucose_log where id = :id")
    fun get(id: Int): Single<GlucoseLog>
}