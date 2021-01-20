package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.axel_stein.glucose_tracker.data.model.A1cLog

@Dao
abstract class A1cLogDao : BaseDao<A1cLog>() {
    @Query("delete from a1c_log where id = :id")
    abstract fun deleteById(id: Long)

    @Query("delete from a1c_log")
    abstract fun deleteAll()

    @Query("select * from a1c_log where id = :id")
    abstract fun getById(id: Long): A1cLog

    @Query("select * from a1c_log")
    abstract fun getAll(): List<A1cLog>

    @Query("select * from a1c_log where date_time > date('now', '-1 year')")
    abstract fun getByThisYear(): List<A1cLog>

    @Transaction
    open fun importBackup(backup: List<A1cLog>) {
        deleteAll()
        insert(backup)
    }
}