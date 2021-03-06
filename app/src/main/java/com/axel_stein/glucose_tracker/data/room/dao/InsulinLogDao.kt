package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.axel_stein.glucose_tracker.data.room.model.InsulinLog
import com.axel_stein.glucose_tracker.data.room.model.InsulinLogEmbedded
import io.reactivex.Completable
import io.reactivex.Single

@Dao
abstract class InsulinLogDao : BaseDao<InsulinLog>() {
    @Query("delete from insulin_log")
    abstract fun deleteAll()

    @Query("delete from insulin_log where id = :id")
    abstract fun deleteById(id: Long): Completable

    @Transaction
    @Query("select * from insulin_log where id = :id")
    abstract fun getById(id: Long): Single<InsulinLogEmbedded>

    @Query("select * from insulin_log")
    abstract fun getAll(): List<InsulinLog>

    @Transaction
    open fun importBackup(backup: List<InsulinLog>) {
        deleteAll()
        insert(backup)
    }
}