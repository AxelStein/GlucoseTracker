package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.axel_stein.glucose_tracker.data.room.model.NoteLog
import io.reactivex.Completable
import io.reactivex.Single

@Dao
abstract class NoteLogDao : BaseDao<NoteLog>() {
    @Query("delete from note_log where id = :id")
    abstract fun deleteById(id: Long): Completable

    @Query("delete from note_log")
    abstract fun deleteAll()

    @Query("select * from note_log where id = :id")
    abstract fun get(id: Long): Single<NoteLog>

    @Query("select * from note_log")
    abstract fun get(): List<NoteLog>

    @Transaction
    open fun importBackup(backup: List<NoteLog>) {
        deleteAll()
        insert(backup)
    }
}