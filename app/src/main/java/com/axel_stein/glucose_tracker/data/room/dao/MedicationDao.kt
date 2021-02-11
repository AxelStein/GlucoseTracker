package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.axel_stein.glucose_tracker.data.room.model.Medication
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
abstract class MedicationDao : BaseDao<Medication>() {
    @Query("delete from medication_list")
    abstract fun deleteAll()

    @Query("delete from medication_list where id = :id")
    abstract fun deleteById(id: Long): Completable

    @Query("select * from medication_list where id = :id")
    abstract fun getById(id: Long): Single<Medication>

    @Query("update medication_list set active = :active where id = :id")
    abstract fun setActive(id: Long, active: Boolean): Completable

    @Query("select * from medication_list order by title")
    abstract fun observeItems(): Flowable<List<Medication>>

    @Query("select * from medication_list where active = 1 order by title")
    abstract fun getActiveItems(): Single<List<Medication>>

    @Query("select * from medication_list order by title")
    abstract fun getItems(): Single<List<Medication>>

    @Query("select * from medication_list")
    abstract fun getAll(): List<Medication>

    @Transaction
    open fun importBackup(backup: List<Medication>) {
        deleteAll()
        insert(backup)
    }
}