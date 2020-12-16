package com.example.glucose_tracker.data.room.dao

import androidx.room.*
import com.example.glucose_tracker.data.model.GlucoseLog
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface GlucoseLogDao {
    @Insert
    fun insert(log: GlucoseLog): Completable

    @Update
    fun update(log: GlucoseLog): Completable

    @Delete
    fun delete(log: GlucoseLog): Completable

    @Query("delete from glucose_log where id = :id")
    fun deleteById(id: Long): Completable

    @Query("select * from glucose_log where id = :id")
    fun get(id: Long): Single<GlucoseLog>
}