package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.*
import com.axel_stein.glucose_tracker.data.model.InsulinLog
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface InsulinLogDao {
    @Insert
    fun insert(item: InsulinLog): Completable

    @Insert
    fun insert(items: List<InsulinLog>)

    @Update
    fun update(item: InsulinLog): Completable

    @Delete
    fun delete(item: InsulinLog): Completable

    @Query("delete from insulin_log")
    fun deleteAll()

    @Query("delete from insulin_log where id = :id")
    fun deleteById(id: Long): Completable

    @Query("select * from insulin_log where id = :id")
    fun get(id: Long): Single<InsulinLog>

    @Transaction
    fun importBackup(backup: List<InsulinLog>) {
        deleteAll()
        insert(backup)
    }
}