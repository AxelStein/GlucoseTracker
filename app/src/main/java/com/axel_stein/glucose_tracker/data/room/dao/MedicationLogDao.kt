package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.*
import com.axel_stein.glucose_tracker.data.model.MedicationLog
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface MedicationLogDao {
    @Insert
    fun insert(item: MedicationLog): Completable

    @Insert
    fun insert(items: List<MedicationLog>)

    @Update
    fun update(item: MedicationLog): Completable

    @Query("delete from medication_log")
    fun deleteAll()

    @Query("delete from medication_log where id = :id")
    fun deleteById(id: Long): Completable

    @Query("select * from medication_log where id = :id")
    fun get(id: Long): Single<MedicationLog>

    @Transaction
    fun importBackup(backup: List<MedicationLog>) {
        deleteAll()
        insert(backup)
    }
}