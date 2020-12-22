package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.*
import com.axel_stein.glucose_tracker.data.model.GlucoseLog
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface GlucoseLogDao {
    @Insert
    fun insert(log: GlucoseLog): Completable

    @Insert
    fun insert(list: List<GlucoseLog>)

    @Update
    fun update(log: GlucoseLog): Completable

    @Delete
    fun delete(log: GlucoseLog): Completable

    @Query("delete from glucose_log")
    fun deleteAll()

    @Query("delete from glucose_log where id = :id")
    fun deleteById(id: Long): Completable

    @Query("select * from glucose_log where id = :id")
    fun get(id: Long): Single<GlucoseLog>

    @Query("select * from glucose_log")
    fun get(): List<GlucoseLog>

    @Transaction
    fun importBackup(backup: List<GlucoseLog>) {
        deleteAll()
        insert(backup)
    }
}