package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.axel_stein.glucose_tracker.data.model.Insulin
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
abstract class InsulinDao : BaseDao<Insulin>() {
    @Query("delete from insulin_list")
    abstract fun deleteAll()

    @Query("delete from insulin_list where id = :id")
    abstract fun deleteById(id: Long): Completable

    @Query("select * from insulin_list where id = :id")
    abstract fun getById(id: Long): Single<Insulin>

    @Query("select * from insulin_list order by title")
    abstract fun observeItems(): Flowable<List<Insulin>>

    @Query("select * from insulin_list")
    abstract fun getAll(): List<Insulin>

    @Transaction
    open fun importBackup(backup: List<Insulin>) {
        deleteAll()
        insert(backup)
    }
}