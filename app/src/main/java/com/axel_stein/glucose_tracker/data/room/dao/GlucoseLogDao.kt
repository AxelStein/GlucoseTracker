package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.axel_stein.glucose_tracker.data.model.GlucoseLog

@Dao
abstract class GlucoseLogDao : BaseDao<GlucoseLog>() {
    @Query("delete from glucose_log")
    abstract fun deleteAll()

    @Query("delete from glucose_log where id = :id")
    abstract fun deleteById(id: Long)

    @Query("select * from glucose_log where id = :id")
    abstract fun getById(id: Long): GlucoseLog

    @Query("select * from glucose_log")
    abstract fun getAll(): List<GlucoseLog>

    @Query("select * from glucose_log where date_time > date('now', '-14 day')")
    abstract fun getLastTwoWeeks(): List<GlucoseLog>

    @Query("select * from glucose_log where date_time > date('now', '-1 month')")
    abstract fun getLastMonth(): List<GlucoseLog>

    @Query("select * from glucose_log where date_time > date('now', '-3 month')")
    abstract fun getLastThreeMonths(): List<GlucoseLog>

    @Transaction
    open fun importBackup(backup: List<GlucoseLog>) {
        deleteAll()
        insert(backup)
    }
}