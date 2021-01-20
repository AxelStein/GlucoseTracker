package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.*

abstract class BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(item: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(items: List<T>): List<Long>

    @Update
    abstract fun update(item: T)

    @Delete
    abstract fun delete(item: T)

    @Transaction
    open fun upsert(item: T) {
        val id = insert(item)
        if (id == -1L) {
            update(item)
        }
    }
}