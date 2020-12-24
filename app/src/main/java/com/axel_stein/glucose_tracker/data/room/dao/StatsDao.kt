package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.axel_stein.glucose_tracker.data.stats.Stats
import io.reactivex.Single

@Dao
interface StatsDao {
    @Query("""
        select min(value_mmol) as min_mmol, max(value_mmol) as max_mmol, avg(value_mmol) as avg_mmol,
        min(value_mg) as min_mg, max(value_mg) as max_mg, avg(value_mg) as avg_mg
        from glucose_log where date_time > date('now', '-14 day')
    """)
    fun twoWeeks(): Single<Stats>

    @Query("""
        select min(value_mmol) as min_mmol, max(value_mmol) as max_mmol, avg(value_mmol) as avg_mmol, 
        min(value_mg) as min_mg, max(value_mg) as max_mg, avg(value_mg) as avg_mg 
        from glucose_log where date_time > date('now', '-1 month')
    """)
    fun month(): Single<Stats>

    @Query("""
        select min(value_mmol) as min_mmol, max(value_mmol) as max_mmol, avg(value_mmol) as avg_mmol, 
        min(value_mg) as min_mg, max(value_mg) as max_mg, avg(value_mg) as avg_mg 
        from glucose_log where date_time > date('now', '-3 month')
    """)
    fun threeMonths(): Single<Stats>
}