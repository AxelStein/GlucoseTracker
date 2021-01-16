package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.axel_stein.glucose_tracker.data.model.MedicationLog
import io.reactivex.Completable
import io.reactivex.Single

@Dao
abstract class MedicationLogDao : BaseDao<MedicationLog>() {
    @Query("delete from medication_log")
    abstract fun deleteAll()

    @Query("delete from medication_log where id = :id")
    abstract fun deleteById(id: Long): Completable

    @Query("select * from medication_log where id = :id")
    abstract fun get(id: Long): Single<MedicationLog>

    @Transaction
    open fun importBackup(backup: List<MedicationLog>) {
        deleteAll()
        insert(backup)
    }
}