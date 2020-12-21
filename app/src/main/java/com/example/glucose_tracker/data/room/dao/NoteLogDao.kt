package com.example.glucose_tracker.data.room.dao

import androidx.room.*
import com.example.glucose_tracker.data.model.NoteLog
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface NoteLogDao {
    @Insert
    fun insert(log: NoteLog): Completable

    @Insert
    fun insert(list: List<NoteLog>)

    @Update
    fun update(log: NoteLog): Completable

    @Delete
    fun delete(log: NoteLog): Completable

    @Query("delete from note_log where id = :id")
    fun deleteById(id: Long): Completable

    @Query("delete from note_log")
    fun deleteAll()

    @Query("select * from note_log where id = :id")
    fun get(id: Long): Single<NoteLog>

    @Query("select * from note_log")
    fun get(): List<NoteLog>

    @Transaction
    fun importBackup(backup: List<NoteLog>) {
        deleteAll()
        insert(backup)
    }
}