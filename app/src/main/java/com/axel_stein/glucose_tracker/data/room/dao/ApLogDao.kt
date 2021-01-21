package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.axel_stein.glucose_tracker.data.model.ApLog
import io.reactivex.Completable
import io.reactivex.Single

@Dao
abstract class ApLogDao : BaseDao<ApLog>() {
    @Query("delete from ap_log")
    abstract fun deleteAll()

    @Query("delete from ap_log where id = :id")
    abstract fun deleteById(id: Long): Completable

    @Query("select * from ap_log where id = :id")
    abstract fun getById(id: Long): Single<ApLog>

    @Query("select * from ap_log")
    abstract fun getAll(): List<ApLog>

    @Query("select * from ap_log where date_time > date('now', '-14 day')")
    abstract fun getLastTwoWeeks(): List<ApLog>

    @Query("select * from ap_log where date_time > date('now', '-1 month')")
    abstract fun getLastMonth(): List<ApLog>

    @Query("select * from ap_log where date_time > date('now', '-3 month')")
    abstract fun getLastThreeMonths(): List<ApLog>

    @Query("select * from ap_log where date_time > date('now', '-1 year')")
    abstract fun getByThisYear(): List<ApLog>

    @Transaction
    open fun importBackup(backup: List<ApLog>) {
        deleteAll()
        insert(backup)
    }
}