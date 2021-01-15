package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.axel_stein.glucose_tracker.data.model.LogItem
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface LogDao {
    @Query("""
        select id, value_mmol, value_mg, 
            null as kg, null as insulin_id, null as units, 
            null as medication_id, null as amount, measured, 
            date_time, 0 as item_type, null as note, 
            null as foods, null as a1c 
        from glucose_log
        where date_time > date('now', '-3 month') union
        
        select id, null as value_mmol, null as value_mg, 
            null as kg, null as insulin_id, null as units, 
            null as medication_id, null as amount, null as measured, 
            date_time, 2 as item_type, null as note, 
            null as foods, value as a1c 
        from a1c_log 
        where date_time > date('now', '-3 month') union
        
        select id, null as value_mmol, null as value_mg, 
            null as kg, null as insulin_id, null as units, 
            null as medication_id, null as amount, null as measured, 
            date_time, 1 as item_type, note, 
            null as foods, null as a1c 
        from note_log
        where date_time > date('now', '-3 month') union
        
        select id, null as value_mmol, null as value_mg, 
            null as kg, insulin_id, units, 
            null as medication_id, null as amount, measured, 
            date_time, 3 as item_type, null as note, 
            null as foods, null as a1c 
        from insulin_log
        where date_time > date('now', '-3 month') union

        select id, null as value_mmol, null as value_mg, 
            null as kg, null as insulin_id, null as units, 
            medication_id, amount, measured, 
            date_time, 4 as item_type, null as note, 
            null as foods, null as a1c 
        from medication_log
        where date_time > date('now', '-3 month') union

        select id, null as value_mmol, null as value_mg, 
            kg, null as insulin_id, null as units, 
            null as medication_id, null as amount, null as measured, 
            date_time, 5 as item_type, null as note, 
            null as foods, null as a1c 
        from weight_log
        where date_time > date('now', '-3 month')
    """)
    fun getRecentItems(): Flowable<List<LogItem>>

    @Query(""" 
        select id, null as value_mmol, null as value_mg, 
            null as kg, null as insulin_id, null as units, 
            null as medication_id, null as amount, null as measured, 
            date_time, 2 as item_type, null as note, 
            null as foods, value as a1c 
        from a1c_log
    """)
    fun getA1cList(): Flowable<List<LogItem>>

    @Query("""
        select id, value_mmol, value_mg, 
            null as kg, null as insulin_id, null as units, 
            null as medication_id, null as amount, measured, 
            date_time, 0 as item_type, null as note, 
            null as foods, null as a1c 
        from glucose_log
        where substr(date_time, 1, 7) = :yearMonth union
        
        select id, null as value_mmol, null as value_mg, 
            null as kg, null as insulin_id, null as units, 
            null as medication_id, null as amount, null as measured, 
            date_time, 2 as item_type, null as note, 
            null as foods, value as a1c 
        from a1c_log  
        where substr(date_time, 1, 7) = :yearMonth union
        
        select id, null as value_mmol, null as value_mg, 
            null as kg, null as insulin_id, null as units, 
            null as medication_id, null as amount, null as measured, 
            date_time, 1 as item_type, note, 
            null as foods, null as a1c 
        from note_log
        where substr(date_time, 1, 7) = :yearMonth union
        
        select id, null as value_mmol, null as value_mg, 
            null as kg, insulin_id, units, 
            null as medication_id, null as amount, measured, 
            date_time, 3 as item_type, null as note, 
            null as foods, null as a1c 
        from insulin_log
        where substr(date_time, 1, 7) = :yearMonth union

        select id, null as value_mmol, null as value_mg, 
            null as kg, null as insulin_id, null as units, 
            medication_id, amount, measured, 
            date_time, 4 as item_type, null as note, 
            null as foods, null as a1c 
        from medication_log
        where substr(date_time, 1, 7) = :yearMonth union

        select id, null as value_mmol, null as value_mg, 
            kg, null as insulin_id, null as units, 
            null as medication_id, null as amount, null as measured, 
            date_time, 5 as item_type, null as note, 
            null as foods, null as a1c 
        from weight_log
        where substr(date_time, 1, 7) = :yearMonth
    """)
    fun getItems(yearMonth: String): Flowable<List<LogItem>>

    @Query("""
        select substr(date_time, 6, 2) as month from glucose_log
        where substr(date_time, 1, 4) = :year union
        
        select substr(date_time, 6, 2) as month from a1c_log 
        where substr(date_time, 1, 4) = :year union
        
        select substr(date_time, 6, 2) as month from note_log
        where substr(date_time, 1, 4) = :year union

        select substr(date_time, 6, 2) as month from insulin_log
        where substr(date_time, 1, 4) = :year union

        select substr(date_time, 6, 2) as month from medication_log
        where substr(date_time, 1, 4) = :year union

        select substr(date_time, 6, 2) as month from weight_log
        where substr(date_time, 1, 4) = :year
        
        group by month order by month desc
    """)
    fun getMonths(year: String): Single<List<String>>

    @Query("""
        select substr(date_time, 1, 4) as year from glucose_log union
        select substr(date_time, 1, 4) as year from a1c_log union
        select substr(date_time, 1, 4) as year from note_log union
        
        select substr(date_time, 1, 4) as year from insulin_log union
        select substr(date_time, 1, 4) as year from medication_log union
        select substr(date_time, 1, 4) as year from weight_log
        group by year order by year desc
    """)
    fun getYears(): Flowable<List<String>>
}