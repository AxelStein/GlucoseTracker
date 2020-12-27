package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.axel_stein.glucose_tracker.data.model.LogItem
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface LogDao {
    @Query("""
        select id, value_mmol, value_mg, measured, date_time, 0 as item_type, null as note, null as foods, null as a1c from glucose_log
        where date_time > date('now', '-3 month') union
        select id, null as value_mmol, null as value_mg, null as measured, date_time, 2 as item_type, null as note, null as foods, value as a1c from a1c_log 
        where date_time > date('now', '-3 month') union
        select id, null as value_mmol, null as value_mg, null as measured, date_time, 1 as item_type, note, null as foods, null as a1c from note_log
        where date_time > date('now', '-3 month')
    """)
    fun getRecentItems(): Flowable<List<LogItem>>

    @Query("""
        select id, value_mmol, value_mg, measured, date_time, 0 as item_type, null as note, null as foods, null as a1c from glucose_log
        where strftime('%Y-%m', date_time) = :yearMonth union
        select id, null as value_mmol, null as value_mg, null as measured, date_time, 2 as item_type, null as note, null as foods, value as a1c from a1c_log 
        where strftime('%Y-%m', date_time) = :yearMonth union
        select id, null as value_mmol, null as value_mg, null as measured, date_time, 1 as item_type, note, null as foods, null as a1c from note_log
        where strftime('%Y-%m', date_time) = :yearMonth
    """)
    fun getItems(yearMonth: String): Flowable<List<LogItem>>

    @Query("""
        select strftime('%m', date_time) as month from glucose_log
        where strftime('%Y', date_time) = :year union
        select strftime('%m', date_time) as month from a1c_log 
        where strftime('%Y', date_time) = :year union
        select strftime('%m', date_time) as month from note_log
        where strftime('%Y', date_time) = :year
        group by month order by month desc
    """)
    fun getMonths(year: String): Single<List<String>>

    @Query("""
        select strftime('%Y', date_time) as year from glucose_log union
        select strftime('%Y', date_time) as year from a1c_log union
        select strftime('%Y', date_time) as year from note_log
        group by year order by year desc
    """)
    fun getYears(): Flowable<List<String>>
}