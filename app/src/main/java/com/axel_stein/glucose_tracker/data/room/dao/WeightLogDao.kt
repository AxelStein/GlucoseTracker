package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.axel_stein.glucose_tracker.data.model.WeightLog
import io.reactivex.Completable
import io.reactivex.Single

@Dao
abstract class WeightLogDao : BaseDao<WeightLog>() {
    @Query("delete from weight_log")
    abstract fun deleteAll()

    @Query("delete from weight_log where id = :id")
    abstract fun deleteById(id: Long): Completable

    @Query("select * from weight_log where id = :id")
    abstract fun get(id: Long): Single<WeightLog>

    @Transaction
    open fun importBackup(backup: List<WeightLog>) {
        deleteAll()
        insert(backup)
    }
}