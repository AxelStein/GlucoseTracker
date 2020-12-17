package com.example.glucose_tracker.data.room.dao

import androidx.room.*
import com.example.glucose_tracker.data.model.NoteLog
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface NoteLogDao {
    @Insert
    fun insert(log: NoteLog): Completable

    @Update
    fun update(log: NoteLog): Completable

    @Delete
    fun delete(log: NoteLog): Completable

    @Query("delete from note_log where id = :id")
    fun deleteById(id: Long): Completable

    @Query("select * from note_log where id = :id")
    fun get(id: Long): Single<NoteLog>
}