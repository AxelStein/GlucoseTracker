package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.axel_stein.glucose_tracker.data.room.model.PulseLog
import io.reactivex.Completable
import io.reactivex.Single

@Dao
abstract class PulseLogDao : BaseDao<PulseLog>() {
    @Query("delete from pulse_log")
    abstract fun deleteAll()

    @Query("delete from pulse_log where id = :id")
    abstract fun deleteById(id: Long): Completable

    @Query("select * from pulse_log where id = :id")
    abstract fun getById(id: Long): Single<PulseLog>

    @Query("select * from pulse_log")
    abstract fun getAll(): List<PulseLog>

    @Query("select * from pulse_log where date_time > date('now', '-14 day')")
    abstract fun getLastTwoWeeks(): List<PulseLog>

    @Query("select * from pulse_log where date_time > date('now', '-1 month')")
    abstract fun getLastMonth(): List<PulseLog>

    @Query("select * from pulse_log where date_time > date('now', '-3 month')")
    abstract fun getLastThreeMonths(): List<PulseLog>

    @Query("select * from pulse_log where date_time > date('now', '-1 year')")
    abstract fun getByThisYear(): List<PulseLog>

    @Transaction
    open fun importBackup(backup: List<PulseLog>) {
        deleteAll()
        insert(backup)
    }
}