package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.*
import com.axel_stein.glucose_tracker.data.model.Insulin
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface InsulinDao {
    @Insert
    fun insert(item: Insulin): Completable

    @Update
    fun insert(items: List<Insulin>)

    @Update
    fun update(item: Insulin): Completable

    @Query("delete from insulin_list")
    fun deleteAll()

    @Query("delete from insulin_list where id = :id")
    fun deleteById(id: Long): Completable

    @Query("select * from insulin_list where id = :id")
    fun get(id: Long): Single<Insulin>

    @Query("select * from insulin_list order by title")
    fun observeItems(): Flowable<List<Insulin>>

    @Transaction
    fun importBackup(backup: List<Insulin>) {
        deleteAll()
        insert(backup)
    }
}