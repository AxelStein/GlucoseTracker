package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.*
import com.axel_stein.glucose_tracker.data.model.A1cLog
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface A1cLogDao {
    @Insert
    fun insert(log: A1cLog): Completable

    @Insert
    fun insert(list: List<A1cLog>)

    @Update
    fun update(log: A1cLog): Completable

    @Delete
    fun delete(log: A1cLog): Completable

    @Query("delete from a1c_log where id = :id")
    fun deleteById(id: Long): Completable

    @Query("delete from a1c_log")
    fun deleteAll()

    @Query("select * from a1c_log where id = :id")
    fun get(id: Long): Single<A1cLog>

    @Query("select * from a1c_log")
    fun get(): List<A1cLog>

    @Transaction
    fun importBackup(backup: List<A1cLog>) {
        deleteAll()
        insert(backup)
    }
}