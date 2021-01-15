package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.*
import com.axel_stein.glucose_tracker.data.model.WeightLog
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface WeightLogDao {
    @Insert
    fun insert(item: WeightLog): Completable

    @Insert
    fun insert(items: List<WeightLog>)

    @Update
    fun update(item: WeightLog): Completable

    @Query("delete from weight_log")
    fun deleteAll()

    @Query("delete from weight_log where id = :id")
    fun deleteById(id: Long): Completable

    @Query("select * from weight_log where id = :id")
    fun get(id: Long): Single<WeightLog>

    @Transaction
    fun importBackup(backup: List<WeightLog>) {
        deleteAll()
        insert(backup)
    }
}