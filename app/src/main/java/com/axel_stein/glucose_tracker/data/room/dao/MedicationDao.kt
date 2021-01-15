package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.*
import com.axel_stein.glucose_tracker.data.model.Insulin
import com.axel_stein.glucose_tracker.data.model.Medication
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface MedicationDao {
    @Insert
    fun insert(item: Medication): Completable

    @Insert
    fun insert(items: List<Medication>)

    @Insert
    fun update(item: Medication): Completable

    @Delete
    fun delete(item: Insulin): Completable

    @Query("delete from medication_list")
    fun deleteAll()

    @Query("delete from medication_list where id = :id")
    fun deleteById(id: Long): Completable

    @Query("select * from medication_list where id = :id")
    fun get(id: Long): Single<Medication>

    @Query("update medication_list set active = :active where id = :id")
    fun setActive(id: Long, active: Boolean): Completable

    @Query("select * from medication_list order by title, active desc")
    fun observeItems(): Flowable<List<Medication>>

    @Query("select * from medication_list where active = 1 order by title, active desc")
    fun getActiveItems(): Single<List<Medication>>

    @Transaction
    fun importBackup(backup: List<Medication>) {
        deleteAll()
        insert(backup)
    }
}